package ucf.assignments;
/*
 *  UCF COP3330 Summer 2021 Assignment 4 Solution
 *  Copyright 2021 Ayush Pindoria
 */
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Random;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ucf.assignments.Items_Of_TodoList;
class TestBasicFunctionality
{
	private static TestHarness app;
	private static Random random;

	@BeforeAll
	static void setUpBeforeClass() throws Exception
	{
		app = new TestHarness();
		random = new Random();
	}

	@AfterAll
	static void tearDownAfterClass() throws Exception 
	{
		app = null;
	}

	@BeforeEach
	void setUp() throws Exception 
	{
		app.addNewItem(new Items_Of_TodoList("Task1", LocalDate.now().plusDays(3), false));
		app.addNewItem(new Items_Of_TodoList("Task2", LocalDate.now().plusDays(21), false));
		app.addNewItem(new Items_Of_TodoList("Task3", LocalDate.now().plusDays(5), false));
		app.addNewItem(new Items_Of_TodoList("Task4", LocalDate.now().plusWeeks(3), false));
		app.addNewItem(new Items_Of_TodoList("Task5", LocalDate.now(), false));
		app.sortListByDate(app.getList());
	}

	@AfterEach
	void tearDown() throws Exception
	{
		app.reset();
	}

	@Test
	void testAddItems()
	{
		System.out.println("testAddTasks start");
		
		app.reset();
		assertEquals(0, app.getList().size());
		app.addNewItem(new Items_Of_TodoList("Task1", LocalDate.now(), false));
		app.addNewItem(new Items_Of_TodoList("Task2", LocalDate.now(), false));
		app.addNewItem(new Items_Of_TodoList("Task3", LocalDate.now(), false));
		app.addNewItem(new Items_Of_TodoList("Task4", LocalDate.now(), false));
		app.addNewItem(new Items_Of_TodoList("Task5", LocalDate.now(), false));
		assertEquals(5, app.getList().size());
		
		System.out.println("testAddTasks end");
	}

	@Test
	void testRemoveItems()
	{
		System.out.println("testRemoveTasks start");

		assertEquals(5, app.getList().size());

		app.deleteItem(random.nextInt(app.getList().size()), false);
		app.deleteItem(random.nextInt(app.getList().size()), false);
		app.deleteItem(random.nextInt(app.getList().size()), false);
		app.deleteItem(random.nextInt(app.getList().size()), false);

		assertEquals(1, app.getList().size());

		System.out.println("testRemoveTasks end");
	}

	@Test
	void testMarkItemsAsCompleteAndIncomplete()
	{
		System.out.println("testMarkTasksAsCompleteAndIncomplete start");

		assertEquals(5, app.getList().size());
		assertEquals(0, app.getListDone().size());

		app.markItemAsComplete(random.nextInt(app.getList().size()), true);
		app.markItemAsComplete(random.nextInt(app.getList().size()), true);
		app.markItemAsComplete(random.nextInt(app.getList().size()), true);

		assertEquals(2, app.getList().size());
		assertEquals(3, app.getListDone().size());

		app.markItemAsComplete(random.nextInt(app.getListDone().size()), false);
		app.markItemAsComplete(random.nextInt(app.getListDone().size()), false);

		assertEquals(4, app.getList().size());
		assertEquals(1, app.getListDone().size());

		System.out.println("testMarkTasksAsCompleteAndIncomplete end");
	}


	@Test
	void testDeleteCompleteItems()
	{
		System.out.println("testDeleteCompleteTasks start");

		assertEquals(5, app.getList().size());
		assertEquals(0, app.getListDone().size());

		app.markItemAsComplete(random.nextInt(app.getList().size()), true);
		app.markItemAsComplete(random.nextInt(app.getList().size()), true);
		app.markItemAsComplete(random.nextInt(app.getList().size()), true);

		assertEquals(2, app.getList().size());
		assertEquals(3, app.getListDone().size());

		app.deleteItem(random.nextInt(app.getListDone().size()), true);
		app.deleteItem(random.nextInt(app.getListDone().size()), true);

		assertEquals(2, app.getList().size());
		assertEquals(1, app.getListDone().size());

		System.out.println("testDeleteCompleteTasks end");
	}

	@Test
	void testAddNullItems()
	{
		System.out.println("testAddNullTask start");

		assertTrue(app.getErrorLabel().equals(""));
		assertEquals(5, app.getList().size());

		app.addNewItem(new Items_Of_TodoList("", LocalDate.now(), false));

		assertTrue(app.getErrorLabel().equals("Cannot create an empty task"));
		assertEquals(5, app.getList().size());

		System.out.println("testAddNullTask end");
	}

	@Test
	void testAddItemInPast()
	{
		System.out.println("testAddTaskInPast start");

		assertTrue(app.getErrorLabel().equals(""));
		assertEquals(5, app.getList().size());

		app.addNewItem(new Items_Of_TodoList("Task6", LocalDate.now().minusDays(1), false));

		assertTrue(app.getErrorLabel().equals("Cannot create a task in the past"));
		assertEquals(5, app.getList().size());

		System.out.println("testAddTaskInPast end");
	}

	@Test
	void testAddDuplicateItem()
	{
		System.out.println("testAddDuplicateTask start");
		app.reset();

		assertTrue(app.getErrorLabel().equals(""));
		assertEquals(0, app.getList().size());

		app.addNewItem(new Items_Of_TodoList("Task1", LocalDate.now(), false));

		assertEquals(1, app.getList().size());
		assertTrue(app.getErrorLabel().equals(""));

		app.addNewItem(new Items_Of_TodoList("Task1", LocalDate.now(), false));

		assertEquals(1, app.getList().size());
		assertTrue(app.getErrorLabel().equals("Cannot create duplicate tasks"));

		app.addNewItem(new Items_Of_TodoList("Task2", LocalDate.now(), false));

		assertEquals(2, app.getList().size());
		assertTrue(app.getErrorLabel().equals(""));

		System.out.println("testAddDuplicateTask end");
	}

	/**
	 * This verifies that it is not permitted to mark a complete task as incomplete, if it's due date is in the past
	 */
	@Test
	void testMarkingCompleteItemFromPastAsIncomplete()
	{
		System.out.println("testMarkingCompleteTaskFromPastAsIncomplete start");

		// Create a complete task with due date in the past
		ArrayList<Items_Of_TodoList> listDone = app.getListDone();
		listDone.add(new Items_Of_TodoList("Task1", LocalDate.now().minusWeeks(1), false));
		app.setListDone(listDone);

		assertEquals(5, app.getList().size());
		assertEquals(1, app.getListDone().size());

		app.markItemAsComplete(0, false);

		assertTrue(app.getErrorLabel().equals("Cannot mark a task as incomplete if it's past the due date"));
		assertEquals(5, app.getList().size());
		assertEquals(1, app.getListDone().size());

		System.out.println("testMarkingCompleteTaskFromPastAsIncomplete end");
	}


	@Test
	void testCheckSorting()
	{
		System.out.println("testCheckSorting start");

		for(int i = 0 ; i < app.getList().size() ; i++)
		{
			if(i == 0)
			{
				continue;
			}
			assertFalse(app.getList().get(i).getDueDate().isBefore(app.getList().get(i-1).getDueDate()));
		}

		System.out.println("testCheckSorting end");
	}
}