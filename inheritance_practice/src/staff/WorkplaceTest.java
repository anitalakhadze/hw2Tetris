package staff;

import base.Human;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkplaceTest {

    @Test
    void addWorker() {
        Workplace workplace = new Workplace();
        workplace.addWorker(new HumanEmployee());
        workplace.addWorker(new AlienEmployee());
        workplace.addWorker(new HumanEmployee());

        ((Human)workplace.employees.get(0)).goToSleep();
        new Human().goToSleep();
    }
}