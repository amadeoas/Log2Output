package uk.co.bocaditos.log2xlsx.in.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.jayway.jsonpath.internal.Utils;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.api.model.Status;
import io.fabric8.kubernetes.api.model.StatusDetails;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.CopyOrReadable;
import io.fabric8.kubernetes.client.dsl.ExecListenable;
import io.fabric8.kubernetes.client.dsl.ExecListener;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.Execable;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.TtyExecErrorable;
import io.fabric8.kubernetes.client.dsl.ExecListener.Response;
import uk.co.bocaditos.log2xlsx.in.InputException;


/**
 * JUnit tests for class CloudInput.
 */
public class CloudInputTest {

	@SuppressWarnings("unchecked")
	@Test
	public void test() throws InputException, IOException {
		final String[] args = {
				CloudInput.ARG_HOST			+ "host", 
				CloudInput.ARG_TRUST_CERTS	+ "trust-certs",
				CloudInput.ARG_USERNAME		+ "username",
				CloudInput.ARG_PASSWORD		+ "password",
				CloudInput.ARG_NAMESPACE	+ "namespace",
				CloudInput.ARG_ARTIFACT		+ "artifact",
				CloudInput.ARG_FROM			+ "20250213T05:23:54.011",
				CloudInput.ARG_TO			+ "20250213T05:24:54.011"
		};
		final KubernetesClient client = mock(KubernetesClient.class);
		final MixedOperation<Pod, PodList, PodResource> mix = mock(MixedOperation.class);
		final NonNamespaceOperation<Pod, PodList, PodResource> namespace = mock(NonNamespaceOperation.class);
		final PodList podLs = mock(PodList.class);
		final List<Pod> pods = new ArrayList<>(2);
		final ExecWatch watch = mock(ExecWatch.class);
		final Execable ex = mock(Execable.class);
		final ExecListenable listenable = mock(ExecListenable.class);
		final TtyExecErrorable errorable = mock(TtyExecErrorable.class);
		final ByteArrayInputStream[] ins = {null, null};
		final String[][] lines = {
				{"[0][0][0] Line 1\n[0][0][1] Line 2", "[0][1][0] Line 3\n[0][1][1] Line 4\n"},
				{"[1][0][0] Line 5\n[1][0][1] Line 6"}
			};
		final ByteArrayInputStream[][] inFiless = {
				{new ByteArrayInputStream(lines[0][0].getBytes()), new ByteArrayInputStream(lines[0][1].getBytes())},
				{new ByteArrayInputStream(lines[1][0].getBytes())}
			};
		final CompletableFuture<Integer> future = mock(CompletableFuture.class);
		final Value listener = new Value();
		final CloudInput input;
		ObjectMeta meta;
		Pod pod;
		PodResource rsc;
		CopyOrReadable redable;

		doAnswer(new Answer<Integer>() {

				@Override
				public Integer answer(InvocationOnMock invocation) throws Throwable {
					final int code = 0;
					final String reason = "Completed";
					final Throwable throwable = new Throwable();
					final Response response = new Response() {

						@Override
						public int code() {
							return 1;
						}

						@Override
						public String body() throws IOException {
							return "TEST";
						}
						
					};

					listener.listener.onOpen();
					Thread.sleep(5000);
					listener.listener.onFailure(throwable, response);
					listener.listener.onClose(code, reason);
					listener.listener.onExit(code, new Status("0.00", 0, new StatusDetails(), 
							"kind", "Completed", null, "Completed", reason));

					return code;

				}
			})
			.when(future).join();
		doReturn(future).when(watch).exitCode();
		doReturn(watch).when(ex).exec(any(String[].class));
		doAnswer(new Answer<Execable>() {

				@Override
				public Execable answer(final InvocationOnMock invocation) throws Throwable {
					final Object[] args = invocation.getArguments();

					assertNotNull(args);
					assertEquals(1, args.length);
					listener.listener = (ExecListener) args[0];

					return ex;
				}

			})
			.when(listenable).usingListener(any(ExecListener.class));
		doReturn(listenable).when(errorable).withTTY();

		meta = mock(ObjectMeta.class);
		doReturn("artifact-1").when(meta).getName();
		pod = mock(Pod.class);
		doReturn(meta).when(pod).getMetadata();
		pods.add(pod);
		ins[0] = new ByteArrayInputStream("first.log\nsecond!\nthird.log\n".getBytes());
		rsc = mock(PodResource.class);
		redable = mock(CopyOrReadable.class);
		doReturn(inFiless[0][0]).when(redable).read();
		doReturn(redable).when(rsc).file(eq("first.log"));
		redable = mock(CopyOrReadable.class);
		doReturn(inFiless[0][1]).when(redable).read();
		doReturn(redable).when(rsc).file(eq("third.log"));
		doReturn(errorable).when(rsc).writingOutput(any());
		doReturn(rsc).when(namespace).resource(eq(pod));

		meta = mock(ObjectMeta.class);
		doReturn("artifact-2").when(meta).getName();
		pod = mock(Pod.class);
		doReturn(meta).when(pod).getMetadata();
		pods.add(pod);
		ins[1] = new ByteArrayInputStream("fourth\nfith.log".getBytes());
		rsc = mock(PodResource.class);
		redable = mock(CopyOrReadable.class);
		doReturn(inFiless[1][0]).when(redable).read();
		doReturn(redable).when(rsc).file(eq("fith.log"));
		doReturn(errorable).when(rsc).writingOutput(any());
		doReturn(rsc).when(namespace).resource(eq(pod));

		doReturn(pods).when(podLs).getItems();
		doReturn(podLs).when(namespace).list();
		doReturn(namespace).when(mix).inNamespace(any(String.class));
		doReturn(mix).when(client).pods();

		input = new CloudInput(args) {

			@Override
			KubernetesClient buildClient(final Config config) {
				return client;
			}

			@Override
			ByteArrayInputStream buildIn(final Pod pod, final ByteArrayOutputStream out) {
				final int index = (pod == pods.get(0)) ? 0 : 1;

				return ins[index];
			}

		};

		assertEquals(CloudInput.ID, input.getId());
		for (int podIndex = 0; podIndex < 2; ++podIndex) {
			for (int fileIndex = 0; fileIndex < inFiless[podIndex].length; ++fileIndex) {
				final String[] ls = lines[podIndex][fileIndex].split("\n");

				for (final String line : ls) {
					if (Utils.isEmpty(line)) {
						continue;
					}
					assertEquals(line, input.readLine());
				}
			}
		}

		final Pod p = mock(Pod.class);
		final ObjectMeta m = mock(ObjectMeta.class);

		// pod.getMetadata().getName().startsWith(artifact)
		doReturn("test-1").when(m).getName();
		doReturn(m).when(p).getMetadata();
		input.files("none", "", p);

		input.close();
		assertNull(input.readLine());
	}


	class Value {
	
		ExecListener listener;

	} // end class Value

} // end class CloudInputTest
