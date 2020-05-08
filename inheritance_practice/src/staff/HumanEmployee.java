package staff;

import base.Human;

public class HumanEmployee extends Human implements Employee {
    @Override
    public void doWork() {

    }

    @Override
    public void giveMoney(double amount) {

    }

    @Override
    public void goToSleep() {
        System.out.println("Human employee is working overtime. ");
    }

}
