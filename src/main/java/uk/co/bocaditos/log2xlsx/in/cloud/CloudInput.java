package uk.co.bocaditos.log2xlsx.in.cloud;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.utils.Utils;

import uk.co.bocaditos.log2xlsx.in.Input;
import uk.co.bocaditos.log2xlsx.in.InputException;
import uk.co.bocaditos.utils.cmd.CmdException;
import uk.co.bocaditos.utils.cmd.CmdHelpArgDef;
import uk.co.bocaditos.utils.cmd.CmdHelpArgParamDef;


/**
 * Support to access log files in the cloud.
 */
public class CloudInput extends Input {

    private static final Logger logger = LoggerFactory.getLogger(CloudInput.class);

	static final String ID = "CLOUD";

	static final String ARG_HOST		= "host:";
	static final String ARG_TRUST_CERTS	= "trustCerts:";
	static final String ARG_USERNAME	= "u:";
	static final String ARG_PASSWORD	= "p:";
	static final String ARG_NAMESPACE	= "namespace:";
	static final String ARG_ARTIFACT	= "artifact:";
	static final String ARG_FROM		= "from:";
	static final String ARG_TO			= "to:";
	static final String ARG_DIR			= "dir:";

	public final String DEFAULT_DIR = ".";

	private BufferedReader reader;
	private List<FilePair> files;
	private int indexFile;

	private final KubernetesClient client;
	private NonNamespaceOperation<Pod, PodList, PodResource> namespace;


	public CloudInput(final String... args) throws InputException {
		super(args);

        final Config config =  new ConfigBuilder()
                .withMasterUrl(getArgument(args, ARG_HOST))
                .withTrustCerts(getValue(args, ARG_TRUST_CERTS, true))
                .withUsername(getArgument(args, ARG_USERNAME))
                .withPassword(getArgument(args, ARG_PASSWORD))
                .build();

        this.client = buildClient(config);
		this.files = new ArrayList<>();
		set(args);
	}

	@Override
	public void close() throws IOException {
		if (this.indexFile != -1 && this.indexFile < this.files.size()) {
			synchronized (this) {
				if (this.indexFile != -1) {
					final int indexFile = this.indexFile;
		
					this.indexFile = -1;
					this.files.get(indexFile).close();
				}
			}
		}
	}

	@Override
	public String readLine() throws InputException {
		if (this.reader == null || this.indexFile >= this.files.size()) {
			return null;
		}

		return this.files.get(this.indexFile).readLine();
	}

	@Override
	public String getId() {
		return ID;
	}

	public static void initHelp() throws CmdException {
		new CmdHelpArgDef(Input.CMD_LOGS + " " + ID, "Sets the Cloud log input.", false, 
				new CmdHelpArgParamDef(
						ARG_HOST		+ "<host> " 
						+ ARG_TRUST_CERTS + "<trust-certs> "
						+ ARG_USERNAME	+ "<username> "
						+ ARG_PASSWORD	+ "<password> "
						+ ARG_NAMESPACE	+ "<namespace> "
						+ ARG_ARTIFACT	+ "<artifact> "
						+ ARG_FROM		+ "<from|YYYYMMddTHH:mm:ss.SSSS> "
						+ ARG_TO 		+ "<to|YYYYMMddTHH:mm:ss.SSSS> "
						+ ARG_DIR		+ "<dir>",
						"The name of the host.", true));
	}

	// Needed for JUnit test
	KubernetesClient buildClient(final Config config) {
		return new KubernetesClientBuilder().withConfig(config).build();
	}

	ByteArrayInputStream buildIn(final ByteArrayOutputStream out) {
		return new ByteArrayInputStream(out.toByteArray());
	}
	// End of Needed for JUnit test

	private void set(final String... args) throws InputException {
		final String namespace = getArgument(args, ARG_NAMESPACE);
		final String artifact = getArgument(args, ARG_ARTIFACT);
		final String dir = getValue(args, ARG_DIR, DEFAULT_DIR);

		this.namespace = this.client.pods()
			.inNamespace(namespace);
		for (final Pod pod : this.namespace.list().getItems()) {
			files(artifact, dir, pod);
		}

		if (this.files.size() == 0) {
			this.indexFile = 0;
		} else {
			this.reader = this.files.get(0).open();
		}
	}

	private void files(final String artifact, final String dir, final Pod pod) 
			throws InputException {
		if (!pod.getMetadata().getName().startsWith(artifact)) {
			return;
		}

		final PodResource rsc = this.namespace.resource(pod);
		final CountDownLatch execLatch = new CountDownLatch(1);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();

		try (final ExecWatch execWatch = rsc.writingOutput(out).withTTY().usingListener(
		    	new ExecListener() {

		    		@Override
		            public void onOpen() {
		    			logger.debug("[POD \"{0}\"] Shell was opened for directory \"{1}\"", 
		    					pod.getMetadata(), dir);
		            }

		            @Override
		            public void onFailure(final Throwable throwable, final Response response) {
		                try {
							logger.warn("[POD \"" + pod.getMetadata() + "\"] Failure code " 
									+ response.code() + " " + response.body() + " and directory \"" 
									+ dir + "\"",
									throwable);
						} catch (final IOException ioe) {
							logger.warn("[POD \"" + pod.getMetadata() + "\"] Failure code "
									+ response.code() + " and directory \"" + dir + "\"", 
									throwable);
						}
		                execLatch.countDown();
		            }

		            @Override
		            public void onClose(final int code, final String reason) {
		                logger.warn(
		                		"[POD \"{0}\"] Shell on close: code {1, number} with reson \"{2}\" " 
		                		+ "for rirectpry \"{3}\"", 
		                		pod.getMetadata().getName(), code, reason, dir);
		                execLatch.countDown();
		            }

		            @Override
		            public void onExit(final int code, final Status status) {
		                logger.warn(
		                		"[POD \"{0}\"] Shell on exit: code {1, number} with status \"{2}\" " 
		                		+ "for directory \"{3}\"", 
		                		pod.getMetadata().getName(), code, status, dir);
		            }

		    	}).exec("ls", dir)) {
			execLatch.await(5, TimeUnit.SECONDS);
			this.files.add(new FilePair(pod, out));
		} catch (final InterruptedException ie) {
			throw new InputException(ie, "[POD \"{0}\"] Issue when retrieving files from directory " 
					+ "\"{1}\"", 
					pod.getMetadata().getName(), dir);
		}
	}

	List<String> load(final Pod pod, final ByteArrayOutputStream out) throws InputException {
		final ByteArrayInputStream in = buildIn(out);
		final BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		final List<String> filenames = new ArrayList<>();
		String line;

		try {
			while ((line = reader.readLine()) != null) {
				if (Utils.isNullOrEmpty(line) || !line.endsWith(".log")) {
					continue;
				}
				filenames.add(line);
			}

			return filenames;
		} catch (final IOException ioe) {
			throw new InputException(ioe, "Failed to get files on POD \"{0}\"", 
					pod.getMetadata().getName());
		}
	}


	/**
	 * Support for log files contained in POD.
	 */
	class FilePair {

		private final Pod pod;
		private final List<String> filenames;
		private int iFile;


		FilePair(final Pod pod, final ByteArrayOutputStream out) throws InputException {
			this.iFile = -1;
			this.pod = pod;
			this.filenames = load(pod, out);
		}

		public String readLine() throws InputException {
			if (this.iFile == -1) {
				CloudInput.this.reader = open();
			}

			try {
				String line;

				do {
					line = CloudInput.this.reader.readLine();
					if (Utils.isNullOrEmpty(line)) {
						if (this.iFile >= (this.filenames.size() - 1)) {
							// End of FilePair filenames
							this.iFile = -1;
							if (++CloudInput.this.indexFile >= CloudInput.this.files.size()) {
								return null; // end of CloudInput files
							}

							return CloudInput.this.files.get(CloudInput.this.indexFile).readLine(); // new FilePair
						}
						CloudInput.this.reader = open();
						line = null;
					}
				} while (line == null);

				return line;
			} catch (final IOException ioe) {
				throw new InputException(ioe, 
						"[ POD \"{0}\"] Failure when reading line in file \"{1}\"", 
						this.pod.getMetadata().getName(), this.filenames.get(this.iFile));
			}
		}

		private void close() {
			if (CloudInput.this.reader != null && this.iFile < this.filenames.size()) {
				try {
					final BufferedReader reader = CloudInput.this.reader;

					CloudInput.this.reader = null;
					reader.close();
				} catch (final IOException ioe) {
					logger.warn("[POD \"" + this.pod.getMetadata().getName() + "\"] Failed to close " 
							+ "file \"" + this.filenames.get(this.iFile) + "\"", 
							ioe);
				}
			}
		}

		private BufferedReader open() throws InputException {
			close();

			++this.iFile;
			final PodResource rsc = CloudInput.this.namespace.resource(this.pod);
			final InputStream is = rsc.file(this.filenames.get(this.iFile)).read();

			return new BufferedReader(new InputStreamReader(is));
		}

	} // end class FilePair

} // end class CloudInput
