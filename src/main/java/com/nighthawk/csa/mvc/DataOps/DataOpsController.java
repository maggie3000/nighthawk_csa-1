package com.nighthawk.csa.mvc.DataOps;

import com.nighthawk.csa.utility.ConsoleMethods;
import com.nighthawk.csa.mvc.DataOps.genericDataModel.Alphabet;
import com.nighthawk.csa.mvc.DataOps.genericDataModel.Animal;
import com.nighthawk.csa.mvc.DataOps.genericDataModel.Cupcakes;

import com.nighthawk.csa.mvc.DataOps.LinkedLists.CircleQueue;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

/**
 * Circle Queue Driver takes a list of Objects and puts them into a Queue
 * @author     John Mortensen
 *
 */
@Getter
@Controller  // HTTP requests are handled as a controller, using the @Controller annotation
public class DataOpsController {
    private CircleQueue queue;	// circle queue object
    private int count; // number of objects in circle queue
    //control variables for UI checkboxes and radios
    private boolean animal;
    private Animal.KeyType animalKey;
    private boolean cake;
    private Cupcakes.KeyType cakeKey;
    private boolean alpha;
    private Alphabet.KeyType alphaKey;

    /*
     * Circle queue constructor
     */
    public DataOpsController()
    {
        //circle queue inits
        count = 0;
        queue = new CircleQueue();
    }

    /*
     * Add any array of objects to the queue
     */
    public void addCQueue(Object[] objects)
    {
        ConsoleMethods.println("Add " + objects.length);
        for (Object o : objects)
        {
            queue.add(o);
            ConsoleMethods.println("Add: " + queue.getObject() + " " + queue);
            this.count++;
        }
        ConsoleMethods.println();
    }

    /*
     * Delete/Clear all object in circle queue
     */
    public void deleteCQueue()
    {
        int length = this.count;
        ConsoleMethods.println("Delete " + length);

        for (int i = 0; i<length; i++)
        {
            ConsoleMethods.println("Delete: " + queue.delete() + " " + queue);
            this.count--;
        }
    }

    /*
     * String buffer for each row is created to support Thymeleaf (Interable could be alternative)
     */
    public List<String> getCQList()
    {
        List<String> log = new ArrayList<>();
        //travers each row, halting when first is re-encountered (circle queue halt)
        Object first = queue.getFirstObject();
        do {
            log.add(queue.getObject().toString());
        } while (queue.setNext() != first);
        return log;
    }

    /*
     GET request,, parameters are passed within the URI
     */
    @GetMapping("/mvc/dataops")
    public String data(Model model) {
        //initialize database
        this.count = 0;
        this.queue = new CircleQueue();
        //application specific inits
        //title defaults
        this.animalKey = Animal.KeyType.title;
        Animal.setOrder(this.animalKey);
        this.cakeKey = Cupcakes.KeyType.title;
        Cupcakes.setOrder(this.cakeKey);
        this.alphaKey = Alphabet.KeyType.title;
        Alphabet.setOrder(this.alphaKey);
        //control options
        this.animal = true;
        this.cake = true;
        this.alpha = true;
        //load database
        this.addCQueue(Animal.animalData());
        this.addCQueue(Cupcakes.cupCakeData());
        this.addCQueue(Alphabet.alphabetData());
        //database is not sorted, queue order (FIFO) is default
        model.addAttribute("ctl", this);
        return "mvc/dataops"; //HTML render default condition
    }

    /*
     GET request,, parameters are passed within the URI
     */
    @PostMapping("/mvc/dataops")
    public String dataFilter(
            @RequestParam(value = "animal", required = false) String animal,
            @RequestParam(value = "animalKey") Animal.KeyType animalKey,
            @RequestParam(value = "cake", required = false) String cake,
            @RequestParam(value = "cakeKey") Cupcakes.KeyType cakeKey,
            @RequestParam(value = "alpha", required = false) String alpha,
            @RequestParam(value = "alphaKey", required = false) Alphabet.KeyType alphaKey,
            Model model)
    {
        //re-init database according to check boxes selected
        count = 0;
        queue = new CircleQueue();
        //for each category rebuild database, set presentation and database defaults
        if (animal != null) {
            this.addCQueue(Animal.animalData());  //adding Animal database to queue
            this.animal = true;             //persistent selection from check box selection
            this.animalKey = animalKey;     //persistent enum update from radio button selection
            Animal.setOrder(this.animalKey);
        } else {
            this.animal = false;
        }
        if (cake != null) {
            this.addCQueue(Cupcakes.cupCakeData());
            this.cake = true;
            this.cakeKey = cakeKey;
            Cupcakes.setOrder(this.cakeKey);
        } else {
            this.cake = false;
        }
        if (alpha != null) {
            this.addCQueue(Alphabet.alphabetData());
            this.alpha = true;
            this.alphaKey = alphaKey;
            Alphabet.setOrder(this.alphaKey);
        } else {
            this.alpha = false;
        }
        //sort database according to selected options
        this.queue.insertionSort();
        //render with options
        model.addAttribute("ctl", this);
        return "mvc/dataops";
    }

    /*
     * Show key objects/properties of circle queue
     */
    public void printCQueue()
    {
        //queue and object of queue all print via toString()
        ConsoleMethods.println("Size: " + count);
        ConsoleMethods.println("First Element: " + queue.getFirstObject());
        ConsoleMethods.println("Last Element: " + queue.getLastObject());
        ConsoleMethods.println("Full cqueue: " + queue);
        for (String line : this.getCQList()) {
            ConsoleMethods.println(line);
        }
        ConsoleMethods.println();
    }

    /*
     * Illustrate different Objects that can be placed on same Queue
     */
    public static void main(String[] args)
    {
        //queue
        DataOpsController trial = new DataOpsController();

        //add different types of objects to the same queue
        trial.addCQueue(Animal.animalData());
        trial.addCQueue(Cupcakes.cupCakeData());
        trial.addCQueue(Alphabet.alphabetData());

        //display queue objects in queue order
        ConsoleMethods.println("Add order (all database)");
        trial.printCQueue();

        //sort queue objects by specific element within the object and display in sort order
        Animal.setOrder(Animal.KeyType.name);
        Cupcakes.setOrder(Cupcakes.KeyType.frosting);
        Alphabet.setOrder(Alphabet.KeyType.letter);
        trial.queue.insertionSort();
        ConsoleMethods.println("Sorted order (key only)");
        trial.printCQueue();

        //display queue objects, changing output but not sort
        Animal.setOrder(Animal.KeyType.title);
        Cupcakes.setOrder(Cupcakes.KeyType.title);
        Alphabet.setOrder(Alphabet.KeyType.title);
        ConsoleMethods.println("Retain sorted order (all database)");
        trial.printCQueue();
        trial.queue.insertionSort();
        //display queue objects, changing sort order
        ConsoleMethods.println("Order by database type (all database)");
        trial.printCQueue();

        //delete queue objects
        ConsoleMethods.println("Delete from front (all database)");
        trial.deleteCQueue();
    }

}
