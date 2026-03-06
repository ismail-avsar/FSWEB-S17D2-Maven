package com.workintech.s17d2.rest;

import com.workintech.s17d2.model.*;
import com.workintech.s17d2.tax.Taxable;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/developers")
public class DeveloperController {

    public Map<Integer, Developer> developers;
    private final Taxable taxable;

    @Autowired
    public DeveloperController(Taxable taxable) {
        this.taxable = taxable;
    }

    @PostConstruct
    public void init() {
        developers = new HashMap<>();
    }


    @GetMapping
    public ResponseEntity<List<Developer>> getAll() {
        List<Developer> developerList = new ArrayList<>(developers.values());
        return ResponseEntity.ok(developerList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Developer> getById(@PathVariable int id) {
        Developer developer = developers.get(id);
        if (developer == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(developer);
    }

    @PostMapping
    public ResponseEntity<Developer> save(@RequestBody Developer developer) {
        Developer newDeveloper;
        Experience experience = developer.getExperience();

        if (experience == Experience.JUNIOR) {
            double netSalary = developer.getSalary() - (developer.getSalary() * taxable.getSimpleTaxRate() / 100);
            newDeveloper = new JuniorDeveloper(developer.getId(), developer.getName(), netSalary);
        } else if (experience == Experience.MID) {
            double netSalary = developer.getSalary() - (developer.getSalary() * taxable.getMiddleTaxRate() / 100);
            newDeveloper = new MidDeveloper(developer.getId(), developer.getName(), netSalary);
        } else {
            double netSalary = developer.getSalary() - (developer.getSalary() * taxable.getUpperTaxRate() / 100);
            newDeveloper = new SeniorDeveloper(developer.getId(), developer.getName(), netSalary);
        }

        developers.put(newDeveloper.getId(), newDeveloper);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDeveloper);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Developer> update(@PathVariable int id, @RequestBody Developer developer) {
        developer.setId(id);
        developers.put(id, developer);
        return ResponseEntity.ok(developer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Developer> delete(@PathVariable int id) {
        Developer removed = developers.remove(id);
        if (removed == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(removed);
    }
}
