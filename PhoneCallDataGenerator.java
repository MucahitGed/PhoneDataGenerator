import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class PhoneCallDataGenerator {
    public static void main(String[] args) {
        int numCustomers = 500; // Number of customers (K)
        int numCalls = 200000; // Number of calls (R)
        int maxCallDuration = 3600; // Maximum call duration in seconds
        List<String> callData = generateCallData(numCustomers, numCalls, maxCallDuration); // Generate call data
        writeToFile(callData, "phone_calls.txt"); // Write call data to file

        System.out.println("Random phone call data generated successfully.");

        analyzeCallData("phone_calls.txt");
    }

    // Generate random phone call data
    private static List<String> generateCallData(int numCustomers, int numCalls, int maxCallDuration) {
        Random random = new Random();
        List<String> callData = new ArrayList<>();

        Set<Integer> customerSet = new HashSet<>();

        // Create a set of customer IDs
        for (int i = 0; i < numCustomers; i++) {
            customerSet.add(i + 1);
        }

        // Generate call data for the specified number of calls
        for (int i = 0; i < numCalls; i++) {
            int caller = getRandomCustomer(customerSet, random); // Get a random caller
            int callee = getRandomCallee(customerSet, caller, random); // Get a random callee (different from the
                                                                       // caller)
            int duration = Math.max(1, (int) (random.nextGaussian() * maxCallDuration)); // Generate a random call
                                                                                         // duration

            String call = caller + " " + callee + " " + duration;
            callData.add(call);
        }

        return callData;
    }

    // Get a random customer ID from the given set of customers
    private static int getRandomCustomer(Set<Integer> customerSet, Random random) {
        int index = random.nextInt(customerSet.size());
        Iterator<Integer> iterator = customerSet.iterator();
        int customer = iterator.next();

        // Iterate until reaching the randomly selected index
        for (int i = 0; i < index; i++) {
            customer = iterator.next();
        }

        return customer;
    }

    // Get a random callee ID different from the caller
    private static int getRandomCallee(Set<Integer> customerSet, int caller, Random random) {
        int callee = caller;

        // Generate a random callee until it is different from the caller
        while (callee == caller) {
            callee = getRandomCustomer(customerSet, random);
        }

        return callee;
    }

    // Write the call data to the specified file
    private static void writeToFile(List<String> data, String filePath) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));

            // Write each call data to a new line in the file
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Analyze the call data and generate the required lists
    private static void analyzeCallData(String filePath) {
        List<Customer> customers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] callInfo = line.split(" ");
                int callerId = Integer.parseInt(callInfo[0]);
                int calleeId = Integer.parseInt(callInfo[1]);
                int callDuration = Integer.parseInt(callInfo[2]);

                // Update caller's information
                Customer caller = getCustomer(customers, callerId);
                caller.incrementOutgoingCalls();
                caller.incrementTotalCalls();
                caller.addToTotalCallTime(callDuration);

                // Update callee's information
                Customer callee = getCustomer(customers, calleeId);
                callee.incrementIncomingCalls();
                callee.incrementTotalCalls();
                callee.addToTotalCallTime(callDuration);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        int N = 5; // Number of customers to retrieve

        // Get the list of N customers who talked for the longest time as callers
        List<Customer> longestTimeCallers = getCustomersWithLongestCallTime(customers, N, true);
        System.out.println("Customers who talked for the longest time as callers:");
        for (Customer customer : longestTimeCallers) {
            System.out.println("Customer ID: " + customer.getId() + ", Call Time: " + customer.getTotalCallTime());
        }

        // Get the list of N customers who talked for the longest time as callees
        List<Customer> longestTimeCallees = getCustomersWithLongestCallTime(customers, N, false);
        System.out.println("Customers who talked for the longest time as callees:");
        for (Customer customer : longestTimeCallees) {
            System.out.println("Customer ID: " + customer.getId() + ", Call Time: " + customer.getTotalCallTime());
        }

        // Get the list of N customers who called the largest number of other customers
        List<Customer> mostOutgoingCallers = getCustomersWithMostOutgoingCalls(customers, N);
        System.out.println("Customers who called the largest number of other customers:");
        for (Customer customer : mostOutgoingCallers) {
            System.out.println("Customer ID: " + customer.getId() + ", Outgoing Calls: " + customer.getOutgoingCalls());
        }

        // Get the list of N customers who received calls from the largest number of
        // other customers
        List<Customer> mostIncomingCallees = getCustomersWithMostIncomingCalls(customers, N);
        System.out.println("Customers who received calls from the largest number of other customers:");
        for (Customer customer : mostIncomingCallees) {
            System.out.println("Customer ID: " + customer.getId() + ", Incoming Calls: " + customer.getIncomingCalls());
        }

        // Get the list of N customers who made the largest number of calls
        List<Customer> mostActiveCallers = getCustomersWithMostCalls(customers, N, true);
        System.out.println("Customers who made the largest number of calls:");
        for (Customer customer : mostActiveCallers) {
            System.out.println("Customer ID: " + customer.getId() + ", Total Calls: " + customer.getTotalCalls());
        }

        // Get the list of N customers who received the largest number of calls
        List<Customer> mostCalledCallees = getCustomersWithMostCalls(customers, N, false);
        System.out.println("Customers who received the largest number of calls:");
        for (Customer customer : mostCalledCallees) {
            System.out.println(
                    "Customer ID: " + customer.getId() + ", Total Calls Received: " + customer.getTotalCalls());
        }

        // Get the list of N customers who made the smallest number of calls
        List<Customer> leastActiveCallers = getCustomersWithLeastCalls(customers, N, true);
        System.out.println("Customers who made the smallest number of calls:");
        for (Customer customer : leastActiveCallers) {
            System.out.println("Customer ID: " + customer.getId() + ", Total Calls: " + customer.getTotalCalls());
        }

        // Get the list of N customers who received the smallest number of calls
        List<Customer> leastCalledCallees = getCustomersWithLeastCalls(customers, N, false);
        System.out.println("Customers who received the smallest number of calls:");
        for (Customer customer : leastCalledCallees) {
            System.out.println(
                    "Customer ID: " + customer.getId() + ", Total Calls Received: " + customer.getTotalCalls());
        }
    }

    // Retrieve a customer from the list based on the customer ID
    private static Customer getCustomer(List<Customer> customers, int customerId) {
        for (Customer customer : customers) {
            if (customer.getId() == customerId) {
                return customer;
            }
        }
        // If customer does not exist, create a new customer and add it to the list
        Customer newCustomer = new Customer(customerId);
        customers.add(newCustomer);
        return newCustomer;
    }

    // Get the list of N customers who talked for the longest/shortest time as
    // callers or callees
    private static List<Customer> getCustomersWithLongestCallTime(List<Customer> customers, int N, boolean asCallers) {
        customers.sort(Comparator.comparingLong(Customer::getTotalCallTime));
        if (!asCallers) {
            Collections.reverse(customers);
        }
        return customers.subList(0, Math.min(N, customers.size()));
    }

    // Get the list of N customers who made the largest/smallest number of
    // outgoing/incoming calls
    private static List<Customer> getCustomersWithMostOutgoingCalls(List<Customer> customers, int N) {
        customers.sort(Comparator.comparingInt(Customer::getOutgoingCalls));
        Collections.reverse(customers);
        return customers.subList(0, Math.min(N, customers.size()));
    }

    private static List<Customer> getCustomersWithMostIncomingCalls(List<Customer> customers, int N) {
        customers.sort(Comparator.comparingInt(Customer::getIncomingCalls));
        Collections.reverse(customers);
        return customers.subList(0, Math.min(N, customers.size()));
    }

    // Get the list of N customers who made the largest/smallest number of calls
    private static List<Customer> getCustomersWithMostCalls(List<Customer> customers, int N, boolean asCallers) {
        customers.sort(Comparator.comparingInt(Customer::getTotalCalls));
        if (!asCallers) {
            Collections.reverse(customers);
        }
        return customers.subList(0, Math.min(N, customers.size()));
    }

    private static List<Customer> getCustomersWithLeastCalls(List<Customer> customers, int N, boolean asCallers) {
        customers.sort(Comparator.comparingInt(Customer::getTotalCalls));
        if (asCallers) {
            Collections.reverse(customers);
        }
        return customers.subList(0, Math.min(N, customers.size()));
    }

    // Customer class to store customer information
    private static class Customer {
        private final int id;
        private int outgoingCalls;
        private int incomingCalls;
        private int totalCalls;
        private long totalCallTime;

        public Customer(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public int getOutgoingCalls() {
            return outgoingCalls;
        }

        public int getIncomingCalls() {
            return incomingCalls;
        }

        public int getTotalCalls() {
            return totalCalls;
        }

        public long getTotalCallTime() {
            return totalCallTime;
        }

        public void incrementOutgoingCalls() {
            outgoingCalls++;
        }

        public void incrementIncomingCalls() {
            incomingCalls++;
        }

        public void incrementTotalCalls() {
            totalCalls++;
        }

        public void addToTotalCallTime(int callDuration) {
            totalCallTime += callDuration;
        }
    }
}