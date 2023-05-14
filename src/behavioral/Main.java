package behavioral;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        List<Observer> observers = new ArrayList<>();
        Registry a = new Registry();
        a.setValue(10.0);
        Registry b = new Registry();
        b.setValue(5.0);

        Scanner input = new Scanner(System.in);
        while(true) {
            System.out.print("Set_val[A] Set_val[B] [+]AddObserver [-]RemoveObserver e[X]it > ");
            String in = input.next();
            if (in.equalsIgnoreCase("x")) {
                break;
            }
            else if (in.equals("+")) {
                System.out.print("Set New Observer (A|B)(+|-|*|/) <num>): ");
                String in1 = input.next();
                Registry reg = in1.charAt(0) == 'a' ? a : b;
                Operation op = getOperationByChar(in1.charAt(1));
                Double constant = Double.valueOf(in1.substring(2));
                Observer ob = new Observer(observers.size(), reg.getValue(), op, constant);
                observers.add(ob);
                reg.subscribe(ob);
            } else if (in.equalsIgnoreCase("a") || in.equalsIgnoreCase("b")) {
                System.out.print("Value=");
                Registry reg = in.equalsIgnoreCase("a") ? a : b;
                reg.setValue(Double.valueOf(input.next()));
            } else if (in.equals("-") ) {
                System.out.print("Remove observer (#): ");
                int indexToRemove = input.nextInt();
                Observer ob = observers.get(indexToRemove);
                a.unsubscribe(ob);
                b.unsubscribe(ob);
            }
        }

    }

    private static Operation getOperationByChar(char charOp) {
        if (charOp == '+') {
            return new Addition();
        } else if (charOp == '-') {
            return new Subtraction();
        } else if (charOp == '*') {
            return new Multiplication();
        }
        return new Division();
    }
}
interface Operation {
    Double operate(Double operator1, Double operator2);
}

class Addition implements Operation  {

    @Override
    public Double operate(Double operator1, Double operator2) {
        return operator1 + operator2;
    }
}

class Subtraction implements Operation  {

    @Override
    public Double operate(Double operator1, Double operator2) {
        return operator1 - operator2;
    }
}

class Multiplication implements Operation  {

    @Override
    public Double operate(Double operator1, Double operator2) {
        return operator1 * operator2;
    }
}

class Division implements Operation  {

    @Override
    public Double operate(Double operator1, Double operator2) {
        return operator1 / operator2;
    }
}

class Registry {
    private Double value;
    private Set<Observer> observers = new HashSet<>();

    public Registry() {}

    public void setValue(Double value) {
        this.value = value;
        this.notifyObservers();
    }

    public void subscribe(Observer observer) {
        this.observers.add(observer);
        this.print();
    }

    public void unsubscribe(Observer observer) {
        if (this.observers.remove(observer)) {
            this.print();
        }
    }

    private void notifyObservers() {
        this.observers.stream().sorted(Comparator.comparing(Observer::getIndex)).forEach(x -> x.update(this.value));
    }

    public Double getValue() {
        return this.value;
    }

    public void print() {
        observers.stream().sorted(Comparator.comparing(Observer::getIndex)).forEach(Observer::print);
    }
}

class Observer {

    private final int observerIndexNumber;
    private Double registryValue;
    private Operation operation;
    private Double constant;

    Observer(int index, Double registryValue, Operation operation, Double constant) {
        observerIndexNumber = index;
        this.registryValue = registryValue;
        this.operation = operation;
        this.constant = constant;
    }

    public void update(Double registryValue) {
        this.registryValue = registryValue;
        this.print();
    }

    public Double executeOperation() {
        return this.operation.operate(this.registryValue, this.constant);
    }

    public void print() {
        System.out.printf("observer #%d is %.3f\n", this.observerIndexNumber, this.executeOperation());
    }

    public int getIndex() {
        return observerIndexNumber;
    }
}
