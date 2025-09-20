import java.util.*;

// ===== DOMAIN LAYER =====
class Coffee {
    private final String name;
    private final double price;

    public Coffee(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
}

class OrderItem {
    private final Coffee coffee;
    private final int quantity;

    public OrderItem(Coffee coffee, int quantity) {
        this.coffee = coffee;
        this.quantity = quantity;
    }

    public Coffee getCoffee() { return coffee; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return coffee.getPrice() * quantity; }
}

// ===== APPLICATION LAYER =====
class OrderService {
    private final CoffeeRepository repository;
    private final List<OrderItem> cart = new ArrayList<>();

    public OrderService(CoffeeRepository repository) {
        this.repository = repository;
    }

    // Add coffee to cart by index
    public void addToCart(int coffeeIndex, int quantity) {
        List<Coffee> coffees = repository.getAllCoffees();
        if (coffeeIndex >= 1 && coffeeIndex <= coffees.size()) {
            Coffee coffee = coffees.get(coffeeIndex - 1);
            cart.add(new OrderItem(coffee, quantity));
            System.out.println("Added " + quantity + " x " + coffee.getName() + " to cart.");
        } else {
            System.out.println("Invalid selection.");
        }
    }

    // Show shop menu
    public void showMenu() {
        System.out.println("\nCoffee Menu:");
        List<Coffee> coffees = repository.getAllCoffees();
        for (int i = 0; i < coffees.size(); i++) {
            Coffee c = coffees.get(i);
            System.out.println((i + 1) + ". " + c.getName() + " ($" + c.getPrice() + ")");
        }
    }

    // Show cart (current order)
    public void showCart() {
        if (cart.isEmpty()) {
            System.out.println("\nYour cart is empty.");
            return;
        }

        System.out.println("\nYour Cart:");
        double total = 0;
        for (int i = 0; i < cart.size(); i++) {
            OrderItem item = cart.get(i);
            double itemTotal = item.getTotal();
            total += itemTotal;
            System.out.println((i + 1) + ". " + item.getCoffee().getName() +
                               " x " + item.getQuantity() + " = $" + itemTotal);
        }
        System.out.println("Total: $" + total);
    }

    // Checkout
    public void checkout(String customerName) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Nothing to checkout.");
            return;
        }

        System.out.println("\n🧾 Checkout for " + customerName + ":");
        double grandTotal = 0;
        for (OrderItem item : cart) {
            double total = item.getTotal();
            grandTotal += total;
            System.out.println(item.getCoffee().getName() + " for " + customerName +
                               " (" + item.getQuantity() + " x $" + item.getCoffee().getPrice() +
                               " = $" + total + ")");
        }
        System.out.println("Total: $" + grandTotal);
        System.out.println("Thank you, " + customerName + "! Your order will be ready soon.");
        cart.clear(); // clear cart after checkout
    }
}

// ===== DOMAIN LAYER INTERFACE =====
interface CoffeeRepository {
    List<Coffee> getAllCoffees();
}

// ===== INFRASTRUCTURE LAYER =====
class InMemoryCoffeeRepository implements CoffeeRepository {
    private final List<Coffee> db = new ArrayList<>();

    public InMemoryCoffeeRepository() {
        db.add(new Coffee("Latte", 3.5));
        db.add(new Coffee("Espresso", 2.0));
        db.add(new Coffee("Cappuccino", 4.0));
    }

    @Override
    public List<Coffee> getAllCoffees() {
        return db;
    }
}

// ===== PRESENTATION LAYER =====
public class CoffeeShop {
    public static void main(String[] args) {    
        Scanner scanner = new Scanner(System.in);
        CoffeeRepository repo = new InMemoryCoffeeRepository();
        OrderService service = new OrderService(repo);

        System.out.println("Welcome to Java Coffee Shop!");
        System.out.print("Enter your name: ");
        String customerName = scanner.nextLine();

        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Order Coffee");
            System.out.println("2. View Cart");
            System.out.println("3. Check Out");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            String input = scanner.nextLine();

            switch (input) {
                case "1":
                    service.showMenu();
                    System.out.print("Select coffee by number: ");
                    int coffeeIndex = Integer.parseInt(scanner.nextLine());
                    System.out.print("Enter quantity: ");
                    int quantity = Integer.parseInt(scanner.nextLine());
                    service.addToCart(coffeeIndex, quantity);
                    break;

                case "2":
                    service.showCart();
                    break;
                case "3":
                    service.checkout(customerName);
                    break;

                case "4":
                    System.out.println("Goodbye, " + customerName + "!");
                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }
}
