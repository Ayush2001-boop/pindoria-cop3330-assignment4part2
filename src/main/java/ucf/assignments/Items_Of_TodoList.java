package ucf.assignments;
/*
 *  UCF COP3330 Summer 2021 Assignment 4 Solution
 *  Copyright 2021 Ayush Pindoria
 */
import java.io.Serializable;
import java.time.LocalDate;

public class Items_Of_TodoList implements Serializable
{
    /*
    Declare the attributes:
    dueDate: LocalDate
    completionDate: LocalDate
    description: String
    completed: boolean
    urgent: boolean
     */

    private LocalDate dueDate;
    private LocalDate completionDate;
    private String description;
    private boolean completed;
    private boolean urgent;

    public Items_Of_TodoList(String description, LocalDate date, boolean urgent)
    {
        /*
        Constructor to initialize description, date, and urgent based on the received parameters
        set completionDate to null
        set completed to false
        This is because this is a new entry
         */
        this.description = description;
        this.dueDate = date;
        this.completionDate = null;
        this.completed = false;
        this.urgent = urgent;
    }

    @Override
    public String toString()
    {
        /*
        String[] array : dateArray and completionDateArray

        if(dueDate is not null)
        -add dueDate to dateArray (convert to string and split on the basis of "-")

        if(completionDate is not null)
        -add completionDate to completionDateArray (convert to string and split on the basis of "-")

        if(item is urgent)
        -  if(item is completed)
           - return string completionDateArray + description

        -  else
           - return string "urgent" + description

        else(item's not urgent)
        -  if(item is completed)
           - return string completionDateArray + description + dateArray

        -  else
           - return string dateArray + description
                    */
        String[] dateArray = null;
        String[] completionDateArray = null;
        if(dueDate != null)
        {
            dateArray = dueDate.toString().split("-");
        }
        if(completionDate != null)
        {
            completionDateArray = completionDate.toString().split("-");
        }

        if(urgent)
        {
            if(completed)
            {
                return completionDateArray[0] + "/" + completionDateArray[1] + "/" + completionDateArray[2] +
                        " | URGENT | " + description;
            }else
            {
                return "URGENT | " + description;
            }
        }else
        {
            if(completed)
            {
                return completionDateArray[0] + "/" + completionDateArray[1] + "/" + completionDateArray[2] +
                        " | "+ description + ((dateArray == null)?"": "   (due " +
                        dateArray[0] + "/" + dateArray[1] + "/" + dateArray[2] + ")");
            }else
            {
                return ((dateArray == null)?"": dateArray[0] + "/" + dateArray[1] + "/" + dateArray[2] + " | ") +
                        description;
            }
        }
    }

    public LocalDate getCompletionDate()
    {
        /*
        return completionDate
         */
        return completionDate;
    }

    public void setCompletionDate(LocalDate completionDate)
    {

         /*
        initialize completionDate (this)
         */
        this.completionDate = completionDate;
    }

    public boolean isUrgent()
    {
         /*
        return urgent
         */
        return urgent;
    }

    public void setUrgent(boolean urgent)
    {

        /*
        initialize urgent (this)
         */
        this.urgent = urgent;
    }

    public LocalDate getDueDate()
    {

         /*
        return dueDate
         */
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate)
    {

         /*
        initialize dueDate (this)
         */
        this.dueDate = dueDate;
    }

    public String getDescription()
    {
         /*
        return description
         */
        return description;
    }

    public void setDescription(String description)
    {

                /*
        initialize description (this)
         */
        this.description = description;
    }

    public boolean isCompleted()
    {    /*
        return completed (this)
         */
        return completed;
    }

    public void setCompleted(boolean completed)
    {

          /*
        initialize completed (this)
         */
        this.completed = completed;
    }
}
