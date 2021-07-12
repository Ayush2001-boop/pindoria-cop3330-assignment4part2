package ucf.assignments;
/*
 *  UCF COP3330 Summer 2021 Assignment 4 Solution
 *  Copyright 2021 Ayush Pindoria
 */
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class ItemsGUIController
{   /* Declare attributes to control the secondary window :
        this window will have one todolist displayed along with its title and the items sored in it.
        User can add new new item,
        mark an item as completed
        delete an item or delete all items
        display incomplete and complete items
        items will have a description, dueDate, completionDate, urgent, and completed as its attributes

            @FXML attributes:
            - SplitPane: mainPane, splitPane
            - TextField: descriptionText, newTitle
            - Button: completeButton, addItemButton, deleteButton
            - Label: errorLabel, title
            - Checkbox: noDueDateCheckbox, urgentCheckbox
            - ListView<Items_Of_TodoList>: taskItems, taskItemsDone
            - DatePicker: datePicker

            Normal Attributes:
            ObservableList<Items_Of_TodoList>: items , itemsDone .
            FileChooser fc.
            String: fname (This will store the name of the opened file in the application)
        */
    @FXML
    private SplitPane mainPane;
    @FXML
    private TextField descriptionText;
    @FXML
    private DatePicker datePicker;
    @FXML
    private Button addItemButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button completeButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Label errorLabel;
    @FXML
    private ListView<Items_Of_TodoList> taskItems;
    @FXML
    private ListView<Items_Of_TodoList> taskItemsDone;
    @FXML
    private CheckBox noDueDateCheckbox;
    @FXML
    private CheckBox urgentCheckbox;
    @FXML
    private SplitPane splitPane;

    public ObservableList<Items_Of_TodoList> items = FXCollections.observableArrayList();
    public ObservableList<Items_Of_TodoList> itemsDone = FXCollections.observableArrayList();

    public FileChooser fc = new FileChooser(); //for saving and opening files

    public String fname = "";
    public int index;
    public boolean isopened = false;
    @FXML
    void addNewItem(ActionEvent event)
    {   /*
          if: we are allowed to add an item which does not repeat another item and is not empty
           [Use addItemValidate() for this purpose]

        -  call addItemCommit() to store the data
         */
        if(addItemValidate())
        {
            addItemCommit();
        }
    }

    private boolean addItemValidate()
    {   /*
        if: user enters no description for an item
        - print error message: Cannot create an empty item
          return false

        else if: user chooses a date before the current date
        - print error message: Cannot create a task in the past
          return false

        if: there is duplicate item of same description [use isDuplicate()]
        - print error message: Cannot create a duplicate item
          return false

         else:
         - return true
         */

        if( descriptionText.getText().equals("") )
        {
            printError("Cannot create an empty item");
            return false;
        }else if( datePicker.getValue().isBefore(LocalDate.now()) )
        {
            printError("Cannot create an item in the past");
            return false;
        }
        else if(descriptionText.getText().length() > 256) {
            printError("Cannot create an item with description length > 256");
            return false;
        }



        if(isDuplicate())
        {
            printError("Cannot create duplicate tasks");
            return false;
        }
        return true;
    }

    private void addItemCommit()
    {
        /*
        get the text that user entered into text field
        create a new Items_Of_TodoList and set its description using constructor
        add this item to 'items'
        sort 'items': use sortListByDate() method

        add 'item' to 'taskItems' (Displays items in window)
        set the text field (descriptionText) and errorLabel to ""

        call toggleButtons() to enable all buttons.
        Enable all checkboxes and change the date to current date (datePicker)
         */
        items.add(new Items_Of_TodoList(descriptionText.getText(),
                (noDueDateCheckbox.isSelected() ? null : datePicker.getValue()),
                urgentCheckbox.isSelected()));

        sortListByDate(items);
        taskItems.setItems(items);
        descriptionText.setText("");
        errorLabel.setText("");

        toggleButtons(items.isEmpty() && itemsDone.isEmpty());
        datePicker.setDisable(false);
        noDueDateCheckbox.setSelected(false);
        noDueDateCheckbox.setDisable(false);
        urgentCheckbox.setSelected(false);
        datePicker.setValue(LocalDate.now());
    }

    @FXML
    void markitemsAsComplete(ActionEvent event)
    {
            /*
        Create a null Items_Of_TodoList "task"

        if(the user selected from the incomplete items list)
        - set task to the item which the user selected

        else if(the user selected from the complete items list)
        - set task to the item which the user selected

        if(task is not null)
            - if(task is complete)
                - if(due date is before the current date)
                    print error msg: Cannot mark a task as incomplete if it's past the due date

                - else:
                    - remove task from complete items list
                    - add the task to incomplete items list
                    - sort the incomplete items list: use sortListByDate() method to sort 'items'
                    - update the taskItems using 'items'

            - else
                - remove task from incomplete items list
                - add the task to complete items list
                - update the task.completionDate to current date
                - sort the complete items list: use sortListByDate() method to sort 'itemsDone'
                - update the taskItemsDone using 'itemsDone'
         */
        Items_Of_TodoList task = null;

        if(taskItems.isFocused())
        {
            task = taskItems.getSelectionModel().getSelectedItem();
        }else if(taskItemsDone.isFocused())
        {
            task = taskItemsDone.getSelectionModel().getSelectedItem();
        }

        if(task != null)
        {
            if(task.isCompleted())
            {
                if(task.getDueDate() != null &&
                        task.getDueDate().isBefore(LocalDate.now()))
                {
                    printError("Cannot mark a task as incomplete if it's past the due date");
                    return;
                }
                itemsDone.remove(task);
                items.add(task);
                task.setCompleted(false);
                task.setCompletionDate(null);
                sortListByDate(items);
                taskItems.setItems(items);
            }
            else
            {
                items.remove(task);
                itemsDone.add(task);
                task.setCompleted(true);
                task.setCompletionDate(LocalDate.now());
                sortListByDate(itemsDone);
                taskItemsDone.setItems(itemsDone);
            }
        }
    }

    @FXML
    void deleteItem(ActionEvent event)
    {   /*
        remove the user selected item: either from incomplete(left-side display) or complete items list(right-side display)
        use .remove()
        refresh the incomplete and the complete items list: use .refresh()

        call toggleButtons to enable or disable all the buttons
        clear the item in the display that the user wanted to delete : either from incomplete(left-side display) or complete items list(right-side display)
         */
        items.remove(taskItems.getSelectionModel().getSelectedItem());
        itemsDone.remove(taskItemsDone.getSelectionModel().getSelectedItem());
        taskItems.refresh();
        taskItemsDone.refresh();

        toggleButtons(items.isEmpty() && itemsDone.isEmpty());
        taskItems.getSelectionModel().clearSelection();
        taskItemsDone.getSelectionModel().clearSelection();
    }

    @FXML
    void doneitemsListClicked(MouseEvent event) //If the right side displayed is being used
    {   /*
        if(completed items list is not empty)
        - display "Mark as incomplete" on the completeButton

        if(completed items list is empty)
        - disable complete and delete button
        else
        - enable complete and delete button

        disable all the checkboxes
        disable addItem and updateItem button
        set description text and error label to ""
        set datePicker to current date
        display the completed items list on the right after refreshing

         */
        if(!itemsDone.isEmpty())
        {
            completeButton.setText("Mark as incomplete");
        }

        descriptionText.setText("");
        errorLabel.setText("");
        datePicker.setDisable(false);
        datePicker.setValue(LocalDate.now());

        completeButton.setDisable(itemsDone.isEmpty());
        deleteButton.setDisable(itemsDone.isEmpty());
        addItemButton.setDisable(true);
        updateButton.setDisable(true);
        urgentCheckbox.setDisable(true);
        noDueDateCheckbox.setDisable(true);
        taskItems.getSelectionModel().clearSelection();
    }

    @FXML
    void itemsListClicked(MouseEvent event) //If the left side displayed is being used
    {   /*
        if(incomplete items list is not empty)
        - display "Mark as complete" on the completeButton

        if(incomplete items list is empty)
        - disable complete and delete button
        else
        - enable complete and delete button

        disable the urgentCheckBox
        display the incomplete items list on the right after refreshing

        when the user clicks on an element of taskItems
        then show its data in the description box and datePicker
        and enable the updateButton so that the user can make changes
        and disable the addItemButton

         */
        if(!items.isEmpty())
        {
            completeButton.setText("Mark as complete");
        }

        completeButton.setDisable(items.isEmpty());
        deleteButton.setDisable(items.isEmpty());
        addItemButton.setDisable(false);
        updateButton.setDisable(true);
        urgentCheckbox.setDisable(true);
        taskItemsDone.getSelectionModel().clearSelection();

        if(taskItems.getSelectionModel().getSelectedIndex() >= 0)
        {
            updateButton.setDisable(false);
            addItemButton.setDisable(true);
            index = taskItems.getSelectionModel().getSelectedIndex();
            String descp = items.get(index).getDescription();
            descriptionText.setText(descp);

            LocalDate duedate = items.get(index).getDueDate();
            datePicker.setValue(duedate);

            boolean urgent = items.get(index).isUrgent();
        }

    }

    @FXML
    void addItemClicked(MouseEvent event)
    {   /*
            If instead of clicking on the button the user uses keyboard key Enter we use this method

            Enable the urgentCheckbox
            Create a new Scene in mainPane
            Check if the user pressed Enter key
            If he did then add item to 'items': use enterPressed() method
            */
        urgentCheckbox.setDisable(false);
        taskItems.getSelectionModel().clearSelection();
        taskItemsDone.getSelectionModel().clearSelection();

        Scene scene = (Scene) mainPane.getScene();

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                enterPressed();
            }
        });
    }

    @FXML
    void noDueDateSelected(ActionEvent event)
    {/*
              If(noDueDateCheckbox is selected)
              - disable datePicker

              else
              - enable datePicker
             */
        if(noDueDateCheckbox.isSelected())
        {
            datePicker.setDisable(true);
        }else
        {
            datePicker.setDisable(false);
        }
    }

    @FXML
    void urgentSelected(ActionEvent event)
    {  /*
            If(urgentCheckbox is selected)
              - disable noDueDateCheckbox
              - disable datePicker

              else
              - enable noDueDateCheckbox and keep it as selected
             */
        noDueDateCheckbox.setDisable(urgentCheckbox.isSelected());
        noDueDateCheckbox.setSelected(urgentCheckbox.isSelected());
        datePicker.setDisable(urgentCheckbox.isSelected());
    }


    @FXML
    public void initialize()
    {   /*
       Initialize the primary window to be used
       Also initialize the file directory of this project so that the application data
       is stored in text files inside this same project
       Enable all the buttons and set the date of datePicker to current date
       Divide the pane into its parts and set positions
       Add scroll bar if there are many items in the display


        If a file is opened then automatically save data after application is closed


        */
        String path = System.getProperty("user.dir");
        fc.setInitialDirectory(new File(path));
        datePicker.setValue(LocalDate.now());
       //toggleButtons(items.isEmpty() && itemsDone.isEmpty());
        toggleButtons(false);

        SplitPane.Divider divider = splitPane.getDividers().get(0);
        divider.positionProperty().addListener(new ChangeListener<Number>()
        {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldvalue, Number newvalue )
            {
                divider.setPosition(0.5);
            }
        });


         Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                if(isopened == true) {
                    try {
                        saveItemData(fname);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        taskItems.getSelectionModel().clearSelection();
        taskItemsDone.getSelectionModel().clearSelection();


    }


    public void saveItemData(String filename) throws IOException {    /*
        Open an output file: we are going to write the data as a Byte stream
        The filename is received as a parameter


        Add both the incomplete items list and the items completed list to a file:
        Create a AppData object 'data' which stores both the items lists. It will save
        automatically using the constructor.

        Write the object to the file in bytes

        Use exception handling above

         */

        if(isopened == true)
            new FileOutputStream(fname).close();

        OutputStream ops = null;
        ObjectOutputStream objOps = null;
        try {
            ArrayList<Items_Of_TodoList> list1 = (ArrayList<Items_Of_TodoList>) items.stream().collect(Collectors.toList());
            ArrayList<Items_Of_TodoList> list2 = (ArrayList<Items_Of_TodoList>) itemsDone.stream().collect(Collectors.toList());

            AppData data = new AppData(list1, list2);

            ops = new FileOutputStream(filename);
            objOps = new ObjectOutputStream(ops);
            objOps.writeObject(data);
            objOps.flush();


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            try{
                if(objOps != null) objOps.close();
            } catch (Exception ex){

            }
        }
    }

    public void loadItemData(String filename)
    {   /*
        Open the file : we are going to read the data stored as Byte stream
        the filename is received as a parameter

        if(file is not empty)
        Read a new AppData object from file
        Use this object and store the incomplete items list into 'items'
        and completed items list into 'itemsDone'
        Then use this to update the display lists: taskItems and taskItemsDone

        Use exception handling above

        else(file is empty)
        Clear the ListView and the ObservableLists
        This indicates that the file opened is empty.
         */
        InputStream fileIs = null;
        ObjectInputStream objIs = null;

        File file = new File (filename);
        if(file.length() != 0) {

            try {


                fileIs = new FileInputStream(filename);
                objIs = new ObjectInputStream(fileIs);
                AppData data = (AppData) objIs.readObject();
                items.setAll(data.getList());
                itemsDone.setAll(data.getListDone());
                taskItems.setItems(items);
                taskItemsDone.setItems(itemsDone);

            } catch (FileNotFoundException e) {
                return;
                //e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (objIs != null) objIs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        else
        {
            taskItems.getItems().clear();
            taskItemsDone.getItems().clear();
            items.clear();
            itemsDone.clear();
        }
    }


    public void sortListByDate(ObservableList<Items_Of_TodoList> items)
    {   /*
            To sort items by date

            Use Collections.sort(items, new Comparator<Items_Of_TodoList>()

            Then compare Items_Of_TodoList t1 and Items_Of_TodoList t2 inside the comparator
                -if(t1 is urgent)
                 return -1

                -else if(t2 is urgent)
                 return 1

                -if(t1 has no due date)
                 return 1

                -else if(t2 has no due date)
                 return -1

                -if(t1 due date after/equal to t2 due date)
                 return 1

                -if(t1 due date before t2 due date)
                 return -1


                 return 0;
             */
        Collections.sort(items, new Comparator<Items_Of_TodoList>()
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

    private void toggleButtons(boolean listsEmpty)
    {   /*
        disable or enable the buttons:
        completeButton
        deleteButton
        addItemButton

        based on boolean variable which tells if lists are empty or not.
         */
        completeButton.setDisable(listsEmpty);
        deleteButton.setDisable(listsEmpty);
        addItemButton.setDisable(listsEmpty);
    }

    private boolean isDuplicate()
    {  /*
        Use for loop to iterate over the incomplete items list
        -  if(the description that user entered equals the description of the item at index 'i')
             - if(datePicker is disabled)
               - if(item at index 'i' has no due date or is null)

                      it means we cannot add this list because one already exists
                       so return true



              -else(datePicker is enabled)
                 - if(item at index 'i' is not null and is equal to the datePicker's value)

                       it means we cannot add this list because one already exists
                       so return true


        - else return false
         */
        for(int i = 0; i < items.size(); i++)
        {
            if(descriptionText.getText().equals(items.get(i).getDescription()))
            {
                if(datePicker.isDisabled())
                {
                    if(items.get(i).getDueDate() == null)
                    {
                        return true;
                    }
                }else
                {
                    if(items.get(i).getDueDate() != null &&
                            items.get(i).getDueDate().isEqual(datePicker.getValue()))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void printError(String text)
    {  /*
         set the errorLabel to the text received as parameter
         set the color of errorLabel to red
         */
        errorLabel.setText(text);
        errorLabel.setTextFill(Color.RED);
    }

    private void enterPressed()
    {  /*
        If the user pressed enter while adding a new item
        and if the description has been entered for a new item

        call addNewItem(null)
         */
        if(descriptionText.isFocused())
        {
            addNewItem(null);
        }
    }


    public void saveClicked(ActionEvent actionEvent) throws IOException {   /*
    Open the save file dialog using FileChooser we declared before
    If the user enters a file name and clicks on "save"
    -Get the filename and add ".txt"
    -Pass this filename to saveItemData() and it will save the data to the file
    */
        String filename;
        File savedFile = fc.showSaveDialog(new Stage());
        //fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.*"));

        if(savedFile != null)
        {
            filename = savedFile.getName();
            filename += ".txt";
            saveItemData(filename);
        }

    }

    public void editItemClicked(ActionEvent actionEvent) throws IOException  //when update button is clicked
    {
        /*
        remove the old data from "items" at "index"
        get the text that user entered into text field
        create a new Items_Of_TodoList and set its description using constructor
        add this item to 'items'
        sort 'items': use sortListByDate() method

        add 'item' to 'taskItems' (Displays items in window)
        set the text field (descriptionText) and errorLabel to ""

        call toggleButtons() to enable all buttons.
        Enable all checkboxes and change the date to current date (datePicker)

        Change the data stored in the file:
        remove all the old data
        then add all the new data
         */

        items.remove(index);
        items.add(new Items_Of_TodoList(descriptionText.getText(),
            (noDueDateCheckbox.isSelected() ? null : datePicker.getValue()),
            urgentCheckbox.isSelected()));

        sortListByDate(items);
        taskItems.setItems(items);
        descriptionText.setText("");
        errorLabel.setText("");

        toggleButtons(items.isEmpty() && itemsDone.isEmpty());
        datePicker.setDisable(false);
        noDueDateCheckbox.setSelected(false);
        noDueDateCheckbox.setDisable(false);
        urgentCheckbox.setSelected(false);
        datePicker.setValue(LocalDate.now());

        new FileOutputStream(fname).close();
        saveItemData(fname);


    }

    public void openClicked(ActionEvent actionEvent)
    {
        /*
    Open the open file dialog using FileChooser we declared before
    If the user chooses a file name and clicks on "open"
    -Get the filename and set it to fname
    -Pass this filename to loadItemData() and it will load the data onto the application Screen
    */
        File selectedFile = fc.showOpenDialog(new Stage());
        String filename;
        if(selectedFile != null)
        {
            isopened = true;
            filename = selectedFile.getName();
            fname = filename;
            //filename += ".txt";
            loadItemData(filename);
        }

    }

    public void clearClicked(ActionEvent actionEvent) throws IOException {     /*
    Here clear the completed and incomplete items list
    using .clear()
    Also clear the ListView taskItems and taskItemsDone
    This will delete everything
    */
          taskItems.getItems().clear();
          taskItemsDone.getItems().clear();
          items.clear();
          itemsDone.clear();
        new FileOutputStream(fname).close(); //clear the file that has the data stored in it
    }
}


