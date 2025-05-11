package com.siemens.internship;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class ItemService {
    @Autowired
    private ItemRepository itemRepository;
    private static ExecutorService executor = Executors.newFixedThreadPool(10);
    private List<Item> processedItems = new ArrayList<>();
    private int processedCount = 0;


    public List<Item> findAll() {
        return itemRepository.findAll();
    }

    public Optional<Item> findById(Long id) {
        return itemRepository.findById(id);
    }

    public Item save(Item item) {
        return itemRepository.save(item);
    }

    public void deleteById(Long id) {
        itemRepository.deleteById(id);
    }


    /**
     * Your Tasks
     * Identify all concurrency and asynchronous programming issues in the code
     * Fix the implementation to ensure:
     * All items are properly processed before the CompletableFuture completes
     * Thread safety for all shared state
     * Proper error handling and propagation
     * Efficient use of system resources
     * Correct use of Spring's @Async annotation
     * Add appropriate comments explaining your changes and why they fix the issues
     * Write a brief explanation of what was wrong with the original implementation
     *
     * Hints
     * Consider how CompletableFuture composition can help coordinate multiple async operations
     * Think about appropriate thread-safe collections
     * Examine how errors are handled and propagated
     * Consider the interaction between Spring's @Async and CompletableFuture
     */


    /**
     *  In the presented code there is a risk of race condition.
     *  This might happen because processedItems and processedCount are modified simultaneously.
     * Also, the @Async method can return only void or CompletableFuture<T> if I want an asynchronous result.
     * So:
     * - we keep the annotation @Async;
     * - we modify the returnable result with CompletableFuture
     *
     */
    @Async
    public CompletableFuture<List<Item>> processItemsAsync() {

        List<Long> itemIds = itemRepository.findAllIds(); // all IDs
        List<CompletableFuture<Item>> futures = new ArrayList<>(); //all futures (tasks)

        for (Long id : itemIds) {
            CompletableFuture<Item> future = CompletableFuture.supplyAsync(() -> { //lamda expression
                try {
                    Thread.sleep(100); // time to process each

                    //to avoid NullPointerException
                    Optional<Item> optionalItem = itemRepository.findById(id);
                    if (optionalItem.isEmpty()) {
                        return null;
                    }

                    Item item = optionalItem.get();
                    item.setStatus("PROCESSED"); //we created the setter in Item
                    Item saved = itemRepository.save(item);  //saved if processed
                    processedCount++;
                    System.out.println("Processed items = " + processedCount);
                    return saved;

                } catch (InterruptedException e) {
                    System.out.println("Error while processing item with ID " + id + ": " + e.getMessage());
                    return null;
                }
            }, executor);

            futures.add(future); // adds in list
        }

        // created a CompletableFurure that waits for all tasks to finish
        CompletableFuture<Void> allDone = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        allDone.join(); // waits for all until every async op is done

        // takes info from CompletableFuture without try-catch (future.get())
        for (CompletableFuture<Item> f : futures) {
            Item item = f.join();
            if (item != null) {
                processedItems.add(item);
            }
        }

        return CompletableFuture.completedFuture(processedItems);
    }

}


