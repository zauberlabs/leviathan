
import static org.junit.Assert.*

import java.util.concurrent.Executors

import org.junit.Test

import com.zaubersoftware.leviathan.api.engine.groovy.LeviathanCli;
import com.zaubersoftware.leviathan.api.engine.groovy.Scrapper;

import ar.com.zauber.leviathan.common.ExecutorServiceAsyncUriFetcher
import ar.com.zauber.leviathan.common.fluent.Fetchers
public class TauliaTest {
   
   @Test
   public void testInit() throws Exception {
      //def fetcher = new HTTPClientURIFetcher(new DefaultHttpClient());
      def fetcher = Fetchers.createFixed().register("http://taulia.jobscore.com/list?iframe=1", "taulia.html").build()
      def asyncFetcher = new ExecutorServiceAsyncUriFetcher(Executors.newSingleThreadExecutor())
      assertTrue(new LeviathanCli(new Scrapper(asyncFetcher,fetcher)).run("taulia"));
   }
   
}
   