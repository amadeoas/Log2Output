package uk.co.bocaditos.log2xlsx.in.cloud;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.CopyOrReadable;
import io.fabric8.kubernetes.client.dsl.ExecListenable;
import io.fabric8.kubernetes.client.dsl.ExecWatch;
import io.fabric8.kubernetes.client.dsl.Execable;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import io.fabric8.kubernetes.client.dsl.TtyExecErrorable;
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
		final Pod pod = mock(Pod.class);
		final ObjectMeta meta = mock(ObjectMeta.class);
		final PodResource rsc = mock(PodResource.class);
		final CopyOrReadable redable = mock(CopyOrReadable.class);
		final String lines = "first.log\nsecond!\nthird.log\n";
		final ByteArrayInputStream in = new ByteArrayInputStream(lines.getBytes());
		final List<Pod> pods = new ArrayList<>(1);
		final ExecWatch watch = mock(ExecWatch.class);
		final Execable ex = mock(Execable.class);
		final ExecListenable listenable = mock(ExecListenable.class);
		final TtyExecErrorable errorable = mock(TtyExecErrorable.class);

		doReturn("artifact").when(meta).getName();
		doReturn(meta).when(pod).getMetadata();
		pods.add(pod);
		doReturn(in).when(redable).read();
		doReturn(redable).when(rsc).file(any(String.class));
		doReturn(watch).when(ex).exec(any());
		doReturn(ex).when(listenable).usingListener(any());
		doReturn(listenable).when(errorable).withTTY();
		doReturn(errorable).when(rsc).writingOutput(any());
		doReturn(rsc).when(namespace).resource(eq(pod));
		doReturn(pods).when(podLs).getItems();
		doReturn(podLs).when(namespace).list();
		doReturn(namespace).when(mix).inNamespace(any(String.class));
		doReturn(mix).when(client).pods();
		final CloudInput input = new CloudInput(args) {

			@Override
			KubernetesClient buildClient(final Config config) {
				return client;
			}

			@Override
			ByteArrayInputStream buildIn(final ByteArrayOutputStream out) {
				return in;
			}

		};

		assertEquals(CloudInput.ID, input.getId());
		assertNull(input.readLine());
		input.close();
		assertNull(input.readLine());
	}

} // end class CloudInputTest
