package ucf.assignments;
/*
 *  UCF COP3330 Summer 2021 Assignment 4 Solution
 *  Copyright 2021 Ayush Pindoria
 */
import java.io.Serializable;
import java.util.ArrayList;

public class AppData implements Serializable
{
    /*
     Declare 2 ArrayList of type <Items_Of_TodoList>:
    -list: store incomplete todolist
    -listDone: store complete todolist
     */
    private ArrayList<Items_Of_TodoList> list;
    private ArrayList<Items_Of_TodoList> listDone;


    public AppData(ArrayList<Items_Of_TodoList> list, ArrayList<Items_Of_TodoList> listDone)
    {
         /*
        This is a constructor
        Initialize list and listDone (this)
         */
        this.list = list;
        this.listDone = listDone;

    }

    public ArrayList<Items_Of_TodoList> getList()
    {
         /*
        return list
         */
        return list;
    }

    public ArrayList<Items_Of_TodoList> getListDone()
    {
        /*
        return listDone
         */
        return listDone;
    }
}
