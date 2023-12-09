package test;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import stocker.IStockerTester;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

/**
 * Diese Klasse ist die abstrakte Super-Klasse für den geforderten JUnit-Test.
 * Von dieser Klasse muss eine eigene Klasse abgeleitet werden, in der lediglich
 * die Methode {@link #setUp()} zu überschreiben ist.
 *
 * @author ProPra-Team FernUni Hagen
 */
public abstract class StockerTest {

  /**
   * das Interface für das Testen
   */
  protected IStockerTester stockerTester;

  /**
   * Die Methode {@link #setUp()} ist mit der <code>@BeforeEach</code>-Annotation
   * ausgezeichnet. Das heißt, sie wird vor jedem einzelnen Testlauf aufgerufen.
   *
   * <p>
   * Diese Methode muss überschrieben werden, indem über die Setter-Methode
   * {@link #setStockerTester(IStockerTester)} eine Instanz der Klasse zugewiesen
   * wird, die das Interface {@link IStockerTester} implementiert.
   * </p>
   */
  @BeforeEach
  public abstract void setUp();

  /**
   * Über diese Methode wird der {@link #stockerTester} gesetzt.
   *
   * @param tester eine Instanz der Klasse, die das Interface
   *               {@link IStockerTester} implementiert
   */
  public void setStockerTester(IStockerTester tester) {
    stockerTester = tester;
  }


  @Nested
  class MovingAverageTests {

    @Test
    void getMovingAverageSingleValue() {
      double[] stockData = new double[]{25.5, 26.75, 27.0, 26.5, 27.25};
      double[] gd5 = stockerTester.getMovingAverage(5, stockData);
      assertThat(gd5.length).isOne();
      assertThat(gd5[0]).isEqualTo(26.6);
    }


    @Test
    void getMovingAverageLinearAscent() {
      int dataAmount = 100;
      int n = 20;
      double[] stockData = new double[dataAmount];
      for (int i = 0; i < dataAmount; i++) {
        stockData[i] = i;
      }
      double[] result = stockerTester.getMovingAverage(20, stockData);
      /**
       *  First n values result in first MA value -> result.length should be equal to dataAmount - (n-1)
       */
//      assertEquals(result.length, dataAmount - n + 1);
      assertThat(result.length).isEqualTo(dataAmount - n + 1);

      /**
       *  First n values result in first MA value -> result.length should be equal to dataAmount - (n-1)
       *  Expected result[i] = i + 0.5*(n - 1) = i + 9.5
       *  result[0] = ( stockData[0] + ... + stockData[n - 1] ) / n   -> 0 + 9.5 =  9.5
       *  result[1] = ( stockData[1] + ... + stockData[n] )     / n   -> 1 + 9.5 = 10.5
       *  result[2] = ( stockData[2] + ... + stockData[n + 1] ) / n   -> 2 + 9.5 = 11.5
       *  ...
       */
      double constant = 9.5;
      for (int i = 0; i < result.length; i++) {
        assertThat(result[i]).isEqualTo(i + constant, byLessThan(0.1));
      }
    }

    @Test
    void getMovingAverageTooFewData() {
      double[] stockData = new double[]{1.0, 2.0, 3.0, 4.0, 5.0};
      double[] result1 = stockerTester.getMovingAverage(1, stockData);
      assertThat(result1).isEqualTo(stockData);
      double[] result3 = stockerTester.getMovingAverage(3, stockData);
      assertThat(result3).isEqualTo(new double[]{2.0, 3.0, 4.0});
      double[] result5 = stockerTester.getMovingAverage(5, stockData);
      assertThat(result5).isEqualTo(new double[]{3.0});
      double[] result6 = stockerTester.getMovingAverage(6, stockData);
      assertThat(result6.length).isZero();
    }

    @Test
    void getMovingAverageForLongSeries() {
      int n = 5;
      double[] stockData = new double[]{205.46, 201.99, 197.30, 196.43, 194.22, 191.00, 191.86, 193.05, 197.36, 202.55, 207.88, 212.38, 215.86, 218.07, 224.86, 230.44};
      double[] expectedResult = new double[]{199.08, 196.19, 194.16, 193.31, 193.50, 195.16, 198.54, 202.64, 207.21, 211.35, 215.81, 220.32};
      double[] actualResult = stockerTester.getMovingAverage(n, stockData);
      assertThat(expectedResult.length).isEqualTo(actualResult.length);
      for (int i = 0; i < expectedResult.length; i++) {
        assertThat(expectedResult[i]).isEqualTo(actualResult[i], byLessThan(0.01));
      }
    }

    @Test
    void getMovingAverageAlternative() {
      int n = 20;
      double[] stockData = new double[100];
      for (int i = 0; i < 100; i++)
        stockData[i] = i;

      double[] expectedResult = new double[]{9.5, 10.5, 11.5, 12.5, 13.5, 14.5, 15.5, 16.5, 17.5, 18.5, 19.5, 20.5, 21.5, 22.5, 23.5, 24.5, 25.5, 26.5, 27.5, 28.5, 29.5, 30.5, 31.5, 32.5, 33.5, 34.5, 35.5, 36.5, 37.5, 38.5, 39.5, 40.5, 41.5, 42.5, 43.5, 44.5, 45.5, 46.5, 47.5, 48.5, 49.5, 50.5, 51.5, 52.5, 53.5, 54.5, 55.5, 56.5, 57.5, 58.5, 59.5, 60.5, 61.5, 62.5, 63.5, 64.5, 65.5, 66.5, 67.5, 68.5, 69.5, 70.5, 71.5, 72.5, 73.5, 74.5, 75.5, 76.5, 77.5, 78.5, 79.5, 80.5, 81.5, 82.5, 83.5, 84.5, 85.5, 86.5, 87.5, 88.5, 89.5};
      double[] actualResult = stockerTester.getMovingAverage(n, stockData);
      assertThat(expectedResult.length).isEqualTo(actualResult.length);
      for (int i = 0; i < expectedResult.length; i++) {
        assertThat(expectedResult[i]).isEqualTo(actualResult[i], byLessThan(0.01));
      }
    }
  }

  @Nested
  class BollingerBandsTests {

    @Test
    void getLowerBollingerBandSingleValue() {
      /**
       * gd5 = (25.5 + 26.75 + 27.0 + 26.5 + 27.25) / 5 = 133.0 / 5 = 26.6
       * ((25.5 - 26.6)² + (26.75 - 26.6)² + (27.0 - 26.6)² + (26.5 - 26.6)² + (27.25 - 26.6)²) / 5 = 1.826 / 5 = 0.365
       * Square root of .365 = 0.604
       * lbb = 26.6 - (2 * 0.604) = 25.3916954026
       */
      double[] stockData = new double[]{25.5, 26.75, 27.0, 26.5, 27.25};
      double[] lbb = stockerTester.getLowerBollingerBand(2.0, 5, stockData);
      assertThat(lbb.length).isOne();
      assertThat(lbb[0]).isEqualTo(25.3917, withPrecision(0.0001));
    }

    @Test
    void getUpperBollingerBandSingleValue() {
      /**
       * gd5 = (25.5 + 26.75 + 27.0 + 26.5 + 27.25) / 5 = 133.0 / 5 = 26.6
       * ((25.5 - 26.6)² + (26.75 - 26.6)² + (27.0 - 26.6)² + (26.5 - 26.6)² + (27.25 - 26.6)²) / 5 = 1.826 / 5 = 0.365
       * Square root of .365 = 0.604
       * lbb = 26.6 + (2 * 0.604) = 27.8083045974
       */
      double[] stockData = new double[]{25.5, 26.75, 27.0, 26.5, 27.25};
      double[] ubb = stockerTester.getUpperBollingerBand(2.0, 5, stockData);
      assertThat(ubb.length).isOne();
      assertThat(ubb[0]).isEqualTo(27.8083, withPrecision(0.0001));
    }

    @Test
    void getLowerBollingerBandTwoValues() {
      /**
       * gd5[0] = (25.5 + 26.75 + 27.0 + 26.5 + 27.25) / 5 = 133.0 / 5 = 26.6
       * ((25.5 - 26.6)² + (26.75 - 26.6)² + (27.0 - 26.6)² + (26.5 - 26.6)² + (27.25 - 26.6)²) / 5 = 1.826 / 5 = 0.365
       * Square root of .365 = 0.604
       * lbb[0] = 26.6 - (2 * 0.604) = 25.3916954026
       *
       * gd5[1] = (26.75 + 27.0 + 26.5 + 27.25 + 28.01) / 5 = 135.51 / 5 = 27.102
       * ((26.75 - 27.102)² + (27.0 - 27.102)² + (26.5 - 27.102)² + (27.25 - 27.102)² + (28.01 - 27.102)²) / 5 = 1.34308 / 5 = 0.268616
       * Square root of 0.268616 = 0.518281777
       * lbb[1] = 27.102 - (2 * 0.518281777) = 26.065436446
       */
      double[] stockData = new double[]{25.5, 26.75, 27.0, 26.5, 27.25, 28.01};
      double[] lbb = stockerTester.getLowerBollingerBand(2.0, 5, stockData);
      assertThat(lbb.length).isEqualTo(2);
      assertThat(lbb[0]).isEqualTo(25.3917, withPrecision(0.0001));
      assertThat(lbb[1]).isEqualTo(26.0654, withPrecision(0.0001));
    }

    @Test
    void getUpperBollingerBandTwoValues() {
      /**
       * gd5[0] = 25.5 + 26.75 + 27.0 + 26.5 + 27.25) / 5 = 133.0 / 5 = 26.6
       * ((25.5 - 26.6)² + (26.75 - 26.6)² + (27.0 - 26.6)² + (26.5 - 26.6)² + (27.25 - 26.6)²) / 5 = 1.826 / 5 = 0.365
       * Square root of .365 = 0.604
       * ubb[0] = 26.6 + (2 * 0.604) = 27.8083045974
       *
       * gd5[1] = 26.75 + 27.0 + 26.5 + 27.25 + 28.01) / 5 = 135.51 / 5 = 27.102
       * ((26.75 - 27.102)² + (27.0 - 27.102)² + (26.5 - 27.102)² + (27.25 - 27.102)² + (28.01 - 27.102)²) / 5 = 1.34308 / 5 = 0.268616
       * Square root of 0.268616 = 0.518281777
       * ubb[1] = 27.102 + (2 * 0.518281777) = 28.138563554
       */
      double[] stockData = new double[]{25.5, 26.75, 27.0, 26.5, 27.25, 28.01};
      double[] ubb = stockerTester.getUpperBollingerBand(2.0, 5, stockData);
      assertThat(ubb.length).isEqualTo(2);
      assertThat(ubb[0]).isEqualTo(27.8083, withPrecision(0.0001));
      assertThat(ubb[1]).isEqualTo(28.1386, withPrecision(0.0001));
    }

    @Test
    void getBollingerBandsLongSeries() {
      double f = 2.0;
      int n = 5;
      double[] stockData = new double[]{205.46, 201.99, 197.30, 196.43, 194.22, 191.00, 191.86, 193.05, 197.36, 202.55, 207.88, 212.38, 215.86, 218.07, 224.86, 230.44};
      double[] expectedLower = new double[]{190.93, 188.94, 189.24, 189.51, 189.07, 186.58, 186.56, 188.72, 193.92, 200.18, 204.44, 207.33};
      double[] expectedUpper = new double[]{207.23, 203.44, 199.08, 197.11, 197.93, 203.75, 210.52, 216.56, 220.49, 222.52, 227.18, 233.32};
      double[] actualLower = stockerTester.getLowerBollingerBand(f, n, stockData);
      double[] actualUpper = stockerTester.getUpperBollingerBand(f, n, stockData);

      assertThat(actualUpper.length).isEqualTo(actualLower.length);
      assertThat(actualUpper.length).isEqualTo(expectedUpper.length);
      for (int i = 0; i < expectedUpper.length; i++) {
        assertThat(actualUpper[i]).isEqualTo(expectedUpper[i], byLessThan(0.01));
      }
      assertThat(actualLower.length).isEqualTo(expectedLower.length);
      for (int i = 0; i < expectedLower.length; i++) {
        assertThat(actualLower[i]).isEqualTo(expectedLower[i], byLessThan(0.01));
      }
    }

    @Test
    void getBollingerBandsLinearAscent() {
      double f = 2.0;
      int n = 20;
      int dataAmount = 50;
      double[] stockData = new double[dataAmount];
      for (int i = 0; i < dataAmount; i++) {
        stockData[i] = i;
      }
      double expectedLowerStart = -2.0326;
      double expectedUpperStart = 21.0326;
      int expectedResultCount = dataAmount - n + 1;
      double[] actualLower = stockerTester.getLowerBollingerBand(f, n, stockData);
      double[] actualUpper = stockerTester.getUpperBollingerBand(f, n, stockData);

      assertThat(actualLower.length).as("Result length test").isEqualTo(expectedResultCount);
      assertThat(actualUpper.length).as("BBU.length = BBL.length").isEqualTo(actualLower.length);

      /**
       * Sigma = 5,76623 (constant), GD is 9.5, 10.5, 11.5, ...
       */
      for (int i = 0; i < expectedResultCount; i++) {
        assertThat(actualLower[i]).isEqualTo(expectedLowerStart + i, byLessThan(0.1));
        assertThat(actualUpper[i]).isEqualTo(expectedUpperStart + i, byLessThan(0.1));
      }
    }

    @Test
    void getBollingerBandsAlternative() {
      double f = 2.0;
      int n = 20;
      double[] stockData = new double[100];
      for (int i = 0; i < 100; i++) {
        stockData[i] = i;
      }
      double[] expectedUpper = new double[]{21.0326, 22.0326, 23.0326, 24.0326, 25.0326, 26.0326, 27.0326, 28.0326, 29.0326, 30.0326, 31.0326, 32.0326, 33.0326, 34.0326, 35.0326, 36.0326, 37.0326, 38.0326, 39.0326, 40.0326, 41.0326, 42.0326, 43.0326, 44.0326, 45.0326, 46.0326, 47.0326, 48.0326, 49.0326, 50.0326, 51.0326, 52.0326, 53.0326, 54.0326, 55.0326, 56.0326, 57.0326, 58.0326, 59.0326, 60.0326, 61.0326, 62.0326, 63.0326, 64.0326, 65.0326, 66.0326, 67.0326, 68.0326, 69.0326, 70.0326, 71.0326, 72.0326, 73.0326, 74.0326, 75.0326, 76.0326, 77.0326, 78.0326, 79.0326, 80.0326, 81.0326, 82.0326, 83.0326, 84.0326, 85.0326, 86.0326, 87.0326, 88.0326, 89.0326, 90.0326, 91.0326, 92.0326, 93.0326, 94.0326, 95.0326, 96.0326, 97.0326, 98.0326, 99.0326, 100.0326, 101.0326};
      double[] expectedLower = new double[]{-2.0326, -1.0326, -0.0326, 0.9674, 1.9674, 2.9674, 3.9674, 4.9674, 5.9674, 6.9674, 7.9674, 8.9674, 9.9674, 10.9674, 11.9674, 12.9674, 13.9674, 14.9674, 15.9674, 16.9674, 17.9674, 18.9674, 19.9674, 20.9674, 21.9674, 22.9674, 23.9674, 24.9674, 25.9674, 26.9674, 27.9674, 28.9674, 29.9674, 30.9674, 31.9674, 32.9674, 33.9674, 34.9674, 35.9674, 36.9674, 37.9674, 38.9674, 39.9674, 40.9674, 41.9674, 42.9674, 43.9674, 44.9674, 45.9674, 46.9674, 47.9674, 48.9674, 49.9674, 50.9674, 51.9674, 52.9674, 53.9674, 54.9674, 55.9674, 56.9674, 57.9674, 58.9674, 59.9674, 60.9674, 61.9674, 62.9674, 63.9674, 64.9674, 65.9674, 66.9674, 67.9674, 68.9674, 69.9674, 70.9674, 71.9674, 72.9674, 73.9674, 74.9674, 75.9674, 76.9674, 77.9674};
      double[] actualUpper = stockerTester.getUpperBollingerBand(f, n, stockData);
      double[] actualLower = stockerTester.getLowerBollingerBand(f, n, stockData);

      assertThat(actualUpper.length).isEqualTo(actualLower.length);
      assertThat(actualUpper.length).isEqualTo(expectedUpper.length);
      for (int i = 0; i < expectedUpper.length; i++) {
        assertThat(actualUpper[i]).isEqualTo(expectedUpper[i], byLessThan(0.01));
      }
      assertThat(actualLower.length).isEqualTo(expectedLower.length);
      for (int i = 0; i < expectedLower.length; i++) {
        assertThat(actualLower[i]).isEqualTo(expectedLower[i], byLessThan(0.01));
      }
    }

  }

  @Nested
  class WatchlistTests {
    private List<String> watchlistEntriesToAdd01 = new ArrayList<>() {{
      add("AAPL");
      add("AMZN");
      add("TSLA");
    }};
    private List<String> watchlistEntriesToAdd02 = new ArrayList<>() {{
      add("TSLA");
      add("BNTX");
      add("GOOG");
    }};

    @Test
    void clearWatchlist() {
      stockerTester.clearWatchlist();
      assertThat(stockerTester.getWatchlistStockIds())
          .as("Watchlist should be empty")
          .isEmpty();
    }

    @Test
    void addWatchlistEntries() {
      clearWatchlist();
      List<String> watchlistEntries = new ArrayList<>();
      for (String watchlistEntryToAdd : watchlistEntriesToAdd01) {
        watchlistEntries = addWatchlistEntryHelper(watchlistEntryToAdd);
      }
      assertThat(watchlistEntries).hasSameElementsAs(watchlistEntriesToAdd01);
    }

    @Test
    void addWatchlistEntriesDuplicate() {
      clearWatchlist();
      Set<String> symbolSet = new HashSet<>() {{
        addAll(watchlistEntriesToAdd01);
        addAll(watchlistEntriesToAdd02);
      }};

      List<String> watchlistEntries = new ArrayList<>();
      for (String watchlistEntryToAdd : watchlistEntriesToAdd01) {
        watchlistEntries = addWatchlistEntryHelper(watchlistEntryToAdd);
      }
      assertThat(watchlistEntries.size()).isEqualTo(watchlistEntriesToAdd01.size());
      for (String watchlistEntryToAdd : watchlistEntriesToAdd02) {
        watchlistEntries = addWatchlistEntryHelper(watchlistEntryToAdd);
      }
      assertThat(watchlistEntries.size()).isEqualTo(symbolSet.size());
    }

    @Test
    void removeWatchlistEntry() {
      addWatchlistEntries();

      String entryToRemove = watchlistEntriesToAdd01.get(watchlistEntriesToAdd01.size() - 1);
      assertThat(entryToRemove).isNotEmpty();
      stockerTester.removeWatchlistEntry(entryToRemove);

      String[] entriesAfterRemove = stockerTester.getWatchlistStockIds();
      List<String> expectedWatchlistEntries = new ArrayList<>(watchlistEntriesToAdd01);
      assertThat(expectedWatchlistEntries.remove(entryToRemove)).isTrue();
      assertThat(Arrays.asList(entriesAfterRemove))
          .hasSameElementsAs(expectedWatchlistEntries);
    }

    @Test
    void removeNonExistentWatchlistEntry() {
      addWatchlistEntries();
      String[] entriesAfterAdd = stockerTester.getWatchlistStockIds();
      String entryToRemove = "MSFT";
      stockerTester.removeWatchlistEntry(entryToRemove);
      String[] entriesAfterRemove = stockerTester.getWatchlistStockIds();
      assertThat(entriesAfterAdd.length).isEqualTo(entriesAfterRemove.length);
      assertThat(Arrays.asList(entriesAfterRemove).contains(entryToRemove)).isFalse();
    }

    private List<String> addWatchlistEntryHelper(String stockId) {
      stockerTester.addWatchlistEntry(stockId);
      String[] watchlistEntries = stockerTester.getWatchlistStockIds();
      List<String> actualEntriesList = Arrays.asList(watchlistEntries);
      assertThat(actualEntriesList)
          .as("WL should contain symbol '%s'", stockId)
          .contains(stockId);
      return Arrays.asList(watchlistEntries);
    }
  }

  @Nested
  class AlarmTests {

    private Map<String, List<Double>> alarmEntriesToAdd = new HashMap<>() {{
      put("AAPL", new ArrayList<>() {{
        add(100.0);
      }});
      put("AMZN", new ArrayList<>() {{
        add(200.0);
      }});
      put("TSLA", new ArrayList<>() {{
        add(300.0);
        add(300.0);
        add(400.0);
      }});
      put("GOOG", new ArrayList<>() {{
        add(500.0);
      }});
    }};

    @Test
    void clearAllAlarms() {
      stockerTester.clearAllAlarms();
      assertThat(stockerTester.getAlarmStockIds())
          .as("Alarm list should be empty")
          .isEmpty();
    }

    @Test
    void clearAlarms() {
      addAlarmEntriesHelper();
      for (String stockId : alarmEntriesToAdd.keySet()) {
        stockerTester.clearAlarms(stockId);
      }
      assertThat(stockerTester.getAlarmStockIds())
          .isEmpty();
    }

    @Test
    void addAlarm() {
      addAlarmEntriesHelper();

      assertThat(stockerTester.getAlarmStockIds())
          .hasSameElementsAs(alarmEntriesToAdd.keySet());

      double[] alarms = stockerTester.getAlarms("TSLA");
      assertThat(alarms)
          .as("Amount of stored TSLA alarams")
          .hasSize(2);
    }

    @Test
    void removeAlarm() {
      addAlarmEntriesHelper();
      stockerTester.removeAlarm("TSLA", 300.0);
      double[] alarms = stockerTester.getAlarms("TSLA");
      assertThat(alarms).isEqualTo(new double[]{400.0});
    }

    private void addAlarmEntriesHelper() {
      clearAllAlarms();
      for (Map.Entry<String, List<Double>> entry : alarmEntriesToAdd.entrySet()) {
        String stockId = entry.getKey();
        for (Double value : entry.getValue()) {
          stockerTester.addAlarm(stockId, value);
          double[] alarmEntries = stockerTester.getAlarms(stockId);
          assertThat(alarmEntries)
              .as("Alarms for symbol '%s' should contain value %d", stockId, value)
              .contains(value);
        }
      }
    }
  }

}
