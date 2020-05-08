package staff;

import base.Entity;

import java.util.ArrayList;
import java.util.List;

public class Workplace {
    List<Employee> employees;

    public Workplace() {
        this.employees = new ArrayList<>();
    }

    void addWorker(Employee worker) {
        this.employees.add(worker);
    }
}
