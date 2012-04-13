
import static org.junit.Assert.*

import java.util.concurrent.Executors

import javax.xml.xpath.XPathFactory;

import org.junit.Test
import org.springframework.core.io.ClassPathResource;

import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher
import ar.com.zauber.leviathan.common.fluent.Fetchers
public class TauliaTest {
   
   @Test
   public void testInit() throws Exception {
      //def fetcher = new HTTPClientURIFetcher(new DefaultHttpClient());
      def fetcher = Fetchers.createFixed().register("http://taulia.jobscore.com/list?iframe=1", "taulia.html").build()
      def asyncFetcher = new ExecutorServiceAsyncUriFetcher(Executors.newSingleThreadExecutor())
      assertTrue(new LeviathanCli(fetcher, asyncFetcher).run("taulia"));
   }
   
}
   