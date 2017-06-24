package il.ac.technion.cs.sd.sub.test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import il.ac.technion.cs.sd.sub.app.SubscriberInitializer;
import il.ac.technion.cs.sd.sub.app.SubscriberReader;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class EndToEndTest {
	static Injector injector;

//	@Rule
//	public Timeout globalTimeout = Timeout.seconds(30);

	@After
	public void tearDown() throws Exception {
		injector.getProvider(FakeFactoryProvider.class).get().get().clean();
	}

	@SuppressWarnings("resource")
	private static String setUpFile(String filename) throws FileNotFoundException {
		URL r = EndToEndTest.class.getResource(filename);
		return new Scanner(new File(EndToEndTest.class.getResource(filename).getFile())).useDelimiter("\\Z").next();
	}

	public static void setUp(String fileName) throws Exception {
		injector = Guice.createInjector(new FakeSubscriberModule());
		SubscriberInitializer appInit = injector.getInstance(SubscriberInitializer.class);
		if (fileName.endsWith("csv"))
			appInit.setupCsv(setUpFile(fileName));
		else {
			assert fileName.endsWith("json");
			appInit.setupJson(setUpFile(fileName));
		}
	}

	@Test
	public void userNotFoundShouldHaveNoSubscriptions() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getAllSubscriptions("none").get().entrySet(), empty());
	}

	@Test
	public void userSubscribedToInvalidJournalShouldHaveNoSubscriptions() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getAllSubscriptions("u6").get().entrySet(), empty());
	}

	@Test
	public void userCancelledInvalidJournalShouldHaveNoSubscriptions() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getAllSubscriptions("u6").get().entrySet(), empty());
	}

	@Test
	public void userSubscribedToMultipleJournalsShouldHaveMultipleSubscriptions() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Map<String, List<Boolean>> user1History = reader.getAllSubscriptions("u1").get();
		assertThat(user1History.entrySet(), hasSize(2));
		assertThat(user1History.get("j1"), IsIterableContainingInOrder.contains(true));
		assertThat(user1History.get("j2"), IsIterableContainingInOrder.contains(true));
		Map<String, List<Boolean>> user2History = reader.getAllSubscriptions("u2").get();
		assertThat(user2History.entrySet(), hasSize(2));
		assertThat(user2History.get("j1"), IsIterableContainingInOrder.contains(true, false));
		assertThat(user2History.get("j2"), IsIterableContainingInOrder.contains(true));
	}

	@Test
	public void userSubscribedAndCancelledMultipleTimesShouldHaveOnlyOneCancelledSubscription() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Map<String, List<Boolean>> user5History = reader.getAllSubscriptions("u5").get();
		assertThat(user5History.entrySet(), hasSize(3));
		assertThat(user5History.get("j3"), IsIterableContainingInOrder.contains(true));
		assertThat(user5History.get("j4"), IsIterableContainingInOrder.contains(true));
		assertThat(user5History.get("j5"), IsIterableContainingInOrder.contains(true, false));
	}

	@Test
	public void userMadeLotsOfActionsInAllPossibleCombinationsShouldHaveValidSubscriptions() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Map<String, List<Boolean>> user7History = reader.getAllSubscriptions("u7").get();
		assertThat(user7History.entrySet(), hasSize(6));
		assertThat(user7History.get("j1"),
				IsIterableContainingInOrder.contains(false, true, true, false, true, true, true));
		assertThat(user7History.get("j2"), IsIterableContainingInOrder.contains(true, false, true, true, false));
		assertThat(user7History.get("j3"), IsIterableContainingInOrder.contains(true, false));
		assertThat(user7History.get("j4"), IsIterableContainingInOrder.contains(true, false, true));
		assertThat(user7History.get("j5"), IsIterableContainingInOrder.contains(true, true, true));
		assertThat(user7History.get("j6"), IsIterableContainingInOrder.contains(false));
	}

	@Test
	public void nonExistingUserIsNotAndWasNeverSubscribed() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(Optional.empty(), reader.isSubscribed("none", "j1").get());
		Assert.assertEquals(Optional.empty(), reader.wasSubscribed("none", "j1").get());
		Assert.assertEquals(Optional.empty(), reader.isSubscribed("none", "none").get());
		Assert.assertEquals(Optional.empty(), reader.wasSubscribed("none", "none").get());
	}

	@Test
	public void userSubscribedToInvalidJournalIsNotAndWasNeverSubscribed() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.isSubscribed("u1", "none").get().get());
		Assert.assertFalse(reader.wasSubscribed("u1", "none").get().get());
	}

	@Test
	public void userSubscribedButNeverCancelledIsAndWasSubscribed() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertTrue(reader.isSubscribed("u1", "j1").get().get());
		Assert.assertTrue(reader.wasSubscribed("u1", "j1").get().get());
	}

	@Test
	public void userCancelledSubscriptionIsNotButWasSubscribed() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.isSubscribed("u2", "j1").get().get());
		Assert.assertTrue(reader.wasSubscribed("u2", "j1").get().get());
	}

	@Test
	public void userCancelledButNeverSubscribedIsNotAndWasNeverSubscribed() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.isSubscribed("u7", "j6").get().get());
		Assert.assertFalse(reader.wasSubscribed("u7", "j6").get().get());
	}

	@Test
	public void userCancelledButThenResubscribedIsAndWasSubscribed() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertTrue(reader.isSubscribed("u4", "j4").get().get());
		Assert.assertTrue(reader.wasSubscribed("u4", "j4").get().get());
	}

	@Test
	public void nonExistingUserIsNotAndWasNeverCancelled() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(Optional.empty(), reader.isCanceled("none", "j1").get());
		Assert.assertEquals(Optional.empty(), reader.wasCanceled("none", "j1").get());
		Assert.assertEquals(Optional.empty(), reader.isCanceled("none", "none").get());
		Assert.assertEquals(Optional.empty(), reader.wasCanceled("none", "none").get());
	}

	@Test
	public void userSubscribedToInvalidJournalIsNotAndWasNeverCancelled() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.isCanceled("u1", "none").get().get());
		Assert.assertFalse(reader.wasCanceled("u1", "none").get().get());
	}

	@Test
	public void userSubscribedButNeverCancelledIsNotAndWasNeverCancelled() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.isCanceled("u1", "j1").get().get());
		Assert.assertFalse(reader.wasCanceled("u1", "j1").get().get());
	}

	@Test
	public void userCancelledSubscriptionIsAndWasCancelled() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertTrue(reader.isCanceled("u2", "j1").get().get());
		Assert.assertTrue(reader.wasCanceled("u2", "j1").get().get());
	}

	@Test
	public void userCancelledButNeverSubscribedIsNotAndWasNeverCancelled() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.isCanceled("u7", "j6").get().get());
		Assert.assertFalse(reader.wasCanceled("u7", "j6").get().get());
	}

	@Test
	public void userCancelledButThenResubscribedIsNotButWasCancelled() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.isCanceled("u4", "j4").get().get());
		Assert.assertTrue(reader.wasCanceled("u4", "j4").get().get());
	}

	//////////
	@Test
	public void invalidUserShouldHaveNoSubscribedJournals() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getSubscribedJournals("none").get(), is(empty()));
	}

	@Test
	public void userSubscribedToInvalidJournalShouldHaveNoSubscribedJournals() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getSubscribedJournals("u6").get(), is(empty()));
	}

	@Test
	public void userSubscribedToOneJournalAndNeverCancelledShouldHaveSubscribedJournal() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> u3 = reader.getSubscribedJournals("u3").get();
		assertThat(u3, hasSize(1));
		assertThat(u3, IsIterableContainingInOrder.contains("j3"));
	}

	@Test
	public void userSubscribedToMultipleJournalsAndNeverCancelledShouldHaveSortedSubscribedJournals() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> u1 = reader.getSubscribedJournals("u1").get();
		assertThat(u1, hasSize(2));
		assertThat(u1, IsIterableContainingInOrder.contains("j1", "j2"));
	}

	@Test
	public void userSubscribedButThenCancelledShouldNotHaveThatJournalSubscribed() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> u2 = reader.getSubscribedJournals("u2").get();
		assertThat(u2, hasSize(1));
		assertThat(u2, IsIterableContainingInOrder.contains("j2"));
	}

	@Test
	public void userCancelledButNeverSubscribedShouldHaveNoJournalSubscriptions() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> u8 = reader.getSubscribedJournals("u8").get();
		assertThat(u8, is(empty()));
	}

	@Test
	public void userCancelledButThenResubscribedShouldHaveThatJournalSubscribed() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> u4 = reader.getSubscribedJournals("u4").get();
		assertThat(u4, hasSize(1));
		assertThat(u4, IsIterableContainingInOrder.contains("j4"));
	}

	@Test
	public void userMadeLotsOfActionsInAllPossibleCombinationsShouldHaveJournalsSubscriptions() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> u7 = reader.getSubscribedJournals("u7").get();
		assertThat(u7, hasSize(3));
		assertThat(u7, IsIterableContainingInOrder.contains("j1", "j4", "j5"));
	}

	@Test
	public void invalidUserShouldHaveNoBudget() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.getMonthlyBudget("none").get().isPresent());
	}

	@Test
	public void userSubscribedToInvalidJournalShouldHaveNoBudget() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertFalse(reader.getMonthlyBudget("u6").get().isPresent());
	}

	@Test
	public void userSubscribedToOneJournalAndNeverCancelledShouldHaveBudgetOfTheJournalsPrice() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(3154, reader.getMonthlyBudget("u3").get().getAsInt());
	}

	@Test
	public void userSubscribedToMultipleJournalsAndNeverCancelledShouldHaveBudgetOfAllSubscribedJournals()
			throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(1251, reader.getMonthlyBudget("u1").get().getAsInt());
	}

	@Test
	public void userSubscribedButThenCancelledShouldNotHaveThatJournalIncludedInTheBudget() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(3508, reader.getMonthlyBudget("u5").get().getAsInt());
	}

	@Test
	public void userCancelledButNeverSubscribedShouldHaveBudgetOfZero() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(0, reader.getMonthlyBudget("u8").get().getAsInt());
	}

	@Test
	public void userCancelledAllHisSubscriptionsShouldHaveBudgetOfZero() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(0, reader.getMonthlyBudget("u9").get().getAsInt());
	}

	@Test
	public void userMadeLotsOfActionsInAllPossibleCombinationsShouldHaveBudgetOfTheSubscriptions() throws Exception {
		setUp("ourSmall.csv");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(498, reader.getMonthlyBudget("u7").get().getAsInt());
	}

	@Test
	public void nonExistingJournalShouldHaveNoSubscribers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getSubscribers("none").get().entrySet(), is(empty()));
	}

	@Test
	public void journalWithNoSubscribersShouldHaveNoSubscribers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getSubscribers("j6").get().entrySet(), is(empty()));
	}

	@Test
	public void journalWithOneSubscriptionShouldHaveOneSubscriber() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Map<String, List<Boolean>> j3 = reader.getSubscribers("j3").get();
		assertThat(j3.entrySet(), hasSize(1));
		assertThat(j3.get("u3"), IsIterableContainingInOrder.contains(true));
	}

	@Test
	public void journalWithMultipleSubscriptionsShouldHaveMultipleSubscribers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Map<String, List<Boolean>> j1 = reader.getSubscribers("j1").get();
		assertThat(j1.entrySet(), hasSize(2));
		assertThat(j1.get("u1"), IsIterableContainingInOrder.contains(true));
		assertThat(j1.get("u2"), IsIterableContainingInOrder.contains(true));
	}

	@Test
	public void journalWithNoActiveSubscriptionsShouldHaveOnlyCancelledSubscribers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Map<String, List<Boolean>> j5 = reader.getSubscribers("j5").get();
		assertThat(j5.entrySet(), hasSize(2));
		assertThat(j5.get("u1"), IsIterableContainingInOrder.contains(true, false));
		assertThat(j5.get("u3"), IsIterableContainingInOrder.contains(true, false));
	}

	@Test
	public void journalWithCancelledSubscriptionWithNoSubscirptionShouldHaveSubscriber() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Map<String, List<Boolean>> j4 = reader.getSubscribers("j4").get();
		assertThat(j4.entrySet(), hasSize(1));
		assertThat(j4.get("u4"), IsIterableContainingInOrder.contains(false));
	}

	@Test
	public void journalWithMultipleActionsShouldHaveMultipleSubscribers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Map<String, List<Boolean>> j2 = reader.getSubscribers("j2").get();
		assertThat(j2.entrySet(), hasSize(3));
		assertThat(j2.get("u1"), IsIterableContainingInOrder.contains(false, true, true, false));
		assertThat(j2.get("u2"), IsIterableContainingInOrder.contains(true, true));
		assertThat(j2.get("u3"), IsIterableContainingInOrder.contains(true, false, true));
	}

	@Test
	public void nonExistingJournalShouldHaveNoSubscribedUsers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getSubscribedUsers("none").get(), is(empty()));
	}

	@Test
	public void journalWithNoSubscribersShouldHaveNoSubscribedUsers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		assertThat(reader.getSubscribedUsers("j6").get(), is(empty()));
	}

	@Test
	public void journalWithOneSubscriptionShouldHaveOneSubscribedUser() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> j3 = reader.getSubscribedUsers("j3").get();
		assertThat(j3, hasSize(1));
		assertThat(j3, IsIterableContainingInOrder.contains("u3"));
	}

	@Test
	public void journalWithMultipleSubscriptionsShouldHaveMultipleSortedSubscribedUsers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> j1 = reader.getSubscribedUsers("j1").get();
		assertThat(j1, hasSize(2));
		assertThat(j1, IsIterableContainingInOrder.contains("u1", "u2"));

	}

	@Test
	public void journalWithCancelledSubscriptionsShouldNotHaveCancelledUsers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> j2 = reader.getSubscribedUsers("j2").get();
		assertThat(j2, hasSize(2));
		assertThat(j2, IsIterableContainingInOrder.contains("u2", "u3"));
	}

	@Test
	public void journalWithNoActiveSubscriptionsShouldHaveNoSubscribedUsers() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		List<String> j4 = reader.getSubscribedUsers("j4").get();
		assertThat(j4, is(empty()));
		List<String> j5 = reader.getSubscribedUsers("j5").get();
		assertThat(j5, is(empty()));
	}

	@Test
	public void nonExistingJournalShouldHaveNoIncome() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(OptionalInt.empty(), reader.getMonthlyIncome("none").get());
	}

	@Test
	public void journalWithNoSubscriptionsShouldHaveIncomeOfZero() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(0, reader.getMonthlyIncome("j6").get().getAsInt());
	}

	@Test
	public void journalWithOneSubscriptionShouldHaveIncomeOfItsPrice() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(542, reader.getMonthlyIncome("j3").get().getAsInt());
	}

	@Test
	public void journalWithMultipleSubscriptionShouldHaveIncomeOfItsPriceTimesSubscriptions() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(200, reader.getMonthlyIncome("j1").get().getAsInt());
	}

	@Test
	public void journalWithNoActiveSubscriptionShouldHaveIncomeOfZero() throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(0, reader.getMonthlyIncome("j5").get().getAsInt());
		Assert.assertEquals(0, reader.getMonthlyIncome("j4").get().getAsInt());
	}

	@Test
	public void journalWithMultipleSubscriptionAndCancelationsShouldHaveIncomeOfItsPriceTimesActiveSubscriptions()
			throws Exception {
		setUp("ourSmall.json");
		SubscriberReader reader = injector.getInstance(SubscriberReader.class);
		Assert.assertEquals(1568, reader.getMonthlyIncome("j2").get().getAsInt());
	}
}
