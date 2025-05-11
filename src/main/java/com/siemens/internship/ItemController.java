package com.siemens.internship;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return new ResponseEntity<>(itemService.findAll(), HttpStatus.OK);
    }

    /**
    * The returnable should CREATE and the error should be BAD_REQUEST
    * */
    @PostMapping
    public ResponseEntity<?> createItem(@Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            //we tell the client what is the problem by showing the message error in a list
            List<String> errors = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST); //if we get the errors, we should return a list or an item => <?>
        }
        return new ResponseEntity<>(itemService.save(item), HttpStatus.CREATED);
    }

    /**
     * The returnable should be NOT_FOUND, not NO_CONTENT
     * */
    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * The returnable should be OK and the error should be NOT_FOUND
     * - the original method needs to validate the item => @Valid
     * As the createItem method, firstly we check for errors and then update the item
     * */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @Valid @RequestBody Item item, BindingResult result) {
        if (result.hasErrors()) {
            List<String> errors = new ArrayList<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.add(error.getField() + ": " + error.getDefaultMessage());
            }
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        //valid obj, existing ID
        Optional<Item> existingItem = itemService.findById(id);
        if (existingItem.isPresent()) {
            item.setId(id); //using LOMBOK we have all setters and getters in Item, it works
            return new ResponseEntity<>(itemService.save(item), HttpStatus.OK);
        } else {
            //valid obj, nonexisting ID
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * The returnable should be NOT_FOUND, not CONFLICT
     * - it always says NOT_FOUND even if it works, so we verify the item
     * */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        Optional<Item> item = itemService.findById(id);
        if (item.isPresent()) {
            itemService.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 404
        }
    }

     // if we want to return the result AFTER is finished, we should use join()
    @GetMapping("/process")
    public ResponseEntity<List<Item>> processItems() {
        List<Item> processed = itemService.processItemsAsync().join();
        return new ResponseEntity<>(processed, HttpStatus.OK);
    }
}
