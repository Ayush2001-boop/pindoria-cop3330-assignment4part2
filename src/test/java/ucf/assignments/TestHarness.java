package ucf.assignments;
/*
 *  UCF COP3330 Summer 2021 Assignment 4 Solution
 *  Copyright 2021 Ayush Pindoria
 */
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import ucf.assignments.Items_Of_TodoList;

public class TestHarness
{
	private ArrayList<Items_Of_TodoList> list;
	private ArrayList<Items_Of_TodoList> listDone;
	private String errorLabel;
	private boolean noDueDateSelected;
	private boolean urgentSelected;
	
	public TestHarness()
	{
		reset();
	}
	
	public void reset()
	{
		list = new ArrayList<Items_Of_TodoList>();
		listDone = new ArrayList<Items_Of_TodoList>();
		errorLabel = "";
		noDueDateSelected = false;
		urgentSelected = false;		
	}
	
	public void addNewItem(Items_Of_TodoList task)
	{
		if(addItemValidate(task))
		{
			addItemCommit(task);
		}
	}
	
	private boolean addItemValidate(Items_Of_TodoList task)
	{
		// Check for invalid input
		if( task.getDescription().equals("") )
		{
			errorLabel = "Cannot create an empty task";
			return false;
		}else if( task.getDueDate().isBefore(LocalDate.now()) )
		{
			errorLabel = "Cannot create a task in the past";
			return false;
		}

		// Check for duplicate tasks
		if(isDuplicate(task))
		{
			errorLabel = "Cannot create duplicate tasks";
			return false;
		}
		return true;
	}

	private void addItemCommit(Items_Of_TodoList task)
	{
		list.add(task);
		sortListByDate(list);

		noDueDateSelected = false;
		urgentSelected = false;
		errorLabel = "";
	}
	
	private boolean isDuplicate(Items_Of_TodoList task)
	{
		for(int i = 0; i < list.size(); i++)
		{
			if(task.getDescription().equals(list.get(i).getDescription()))
			{
				if(noDueDateSelected)
				{
					if(list.get(i).getDueDate() == null)
					{
						return true;
					}
				}else
				{
					if(list.get(i).getDueDate() != null && 
					   list.get(i).getDueDate().isEqual(task.getDueDate()))
					{
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public void sortListByDate(ArrayList<Items_Of_TodoList> list)
	{
		Collections.sort(list, new Comparator<Items_Of_TodoList>()
		{
			@Override
			public int compare(Items_Of_TodoList t1, Items_Of_TodoList t2)
			{
				if(t1.isUrgent())
				{
					return -1;
				}else if(t2.isUrgent())
				{
					return 1;
				}
				
				if( t1.getDueDate() == null )
				{
					return 1;
				}else if( t2.getDueDate() == null )
				{
					return -1;
				}

				if( t1.getDueDate().isAfter(t2.getDueDate()) || 
						t1.getDueDate().isEqual(t2.getDueDate()) )
				{
					return 1;
				}
				if( t1.getDueDate().isBefore(t2.getDueDate()) )
				{
					return -1;
				}
				return 0;
			}
		});
	}
	
	
	public void deleteItem(int position, boolean deleteCompletedTask)
	{
		if(deleteCompletedTask)
		{
			if(listDone.size() == 0)
			{
				return;
			}

			listDone.remove(position);
		}else
		{
			if(list.size() == 0)
			{
				return;
			}
			list.remove(position);
		}
	}
	
	public void markItemAsComplete(int position, boolean markAsComplete)
	{
		Items_Of_TodoList task = null;
		if(markAsComplete)
		{
			if(list.size() == 0)
			{
				return;
			}
			task = list.get(position);
			task.setCompleted(true);
			task.setCompletionDate(LocalDate.now());
			listDone.add(task);
			list.remove(position);
			sortListByDate(listDone);
		}else
		{
			if(listDone.size() == 0)
			{
				return;
			}
			task = listDone.get(position);
			
			if(task.getDueDate() != null && 
			   task.getDueDate().isBefore(LocalDate.now()))
			{
				errorLabel = "Cannot mark a task as incomplete if it's past the due date";
				return;
			}
			task.setCompleted(false);
			task.setCompletionDate(null);
			listDone.remove(task);
			list.add(task);
			sortListByDate(list);
		}
	}

	public ArrayList<Items_Of_TodoList> getList() {
		return list;
	}

	public void setList(ArrayList<Items_Of_TodoList> list) {
		this.list = list;
	}

	public ArrayList<Items_Of_TodoList> getListDone() {
		return listDone;
	}

	public void setListDone(ArrayList<Items_Of_TodoList> listDone) {
		this.listDone = listDone;
	}

	public String getErrorLabel() {
		return errorLabel;
	}

	public void setErrorLabel(String errorLabel) {
		this.errorLabel = errorLabel;
	}

	public boolean isNoDueDateSelected() {
		return noDueDateSelected;
	}

	public void setNoDueDateSelected(boolean noDueDateSelected) {
		this.noDueDateSelected = noDueDateSelected;
	}

	public boolean isUrgentSelected() {
		return urgentSelected;
	}

	public void setUrgentSelected(boolean urgentSelected) {
		this.urgentSelected = urgentSelected;
		noDueDateSelected = urgentSelected;
	}
	
}
