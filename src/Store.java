import java.util.*;
import java.util.function.BiConsumer;

public class Store {

    private Map<String, NavigableSet<Item>> storeItems;
    private List<Item> orderList = new ArrayList<>();
    int trackSize = 0;

    public Store() {
        storeItems = new TreeMap<String, NavigableSet<Item>>(Comparator.naturalOrder());
    }

    public void addOrder(Item item) {
        if (!storeItems.containsKey(item.getName())) {
            System.out.println("Item is not available");

            return;
        }

       var itemSet = storeItems.get(item.getName());

//       if (itemSet.contains(item)) {
//           for (var i: itemSet) {
//               if (i.equals(item)) {
//                   itemSet.remove(i);
//                   printDetails.accept(item, item);
//                   System.out.println(item + " Item has been removed from store shelf");
//                   break;
//               }
//
//           }
//
//           orderList.add(item);
//           System.out.println(item + " The order has been added to the order list (cart)");
//       }
        var isItemRemoved = itemSet.removeIf(i -> i.equals(item));
        if(isItemRemoved) {
            printDetails.accept(item,item);
            System.out.println(item + " Item has been removed from store shelf");
            orderList.add(item);
         System.out.println(item + " The order has been added to the order list (cart)");
        }
       else {
           System.out.println("The item requested in not in stock");
       }

    }

    public void addOrder(String name, int qty) {
        if (!storeItems.containsKey(name)) {
            System.out.println("Item is not available");
            return;
        }
        var itemSet = storeItems.get(name);
        qty = itemSet.size() >= qty ? qty : 0;
        if(qty == 0) {
            System.out.println("The order added is more than what we have in stock,\n setting" +
                    " order to the available quantity");
            qty = itemSet.size();
        }

        while (qty > 0) {
            var item = itemSet.pollFirst();
            printDetails.accept(item, item);
            System.out.println(item + " has been removed for store shelf");
           orderList.add(item);
           System.out.println(item + " The Order has been added to the order list (cart)");
           qty--;
        }
    }

    public void addItems(String name, int count) {
        int trackSize = 0;
        if(storeItems.get(name) == null) {
            storeItems.put(name, new TreeSet<>(Comparator.comparing(Item::getId)));
        }
        trackSize = storeItems.get(name).size() + 1;
        for (int i = trackSize; i < trackSize + count; i++)
            storeItems.get(name).add(new Item(i, name));
    }
    public void printItems()  {
       for (var key : storeItems.keySet()) {
           System.out.println("Key => " + key);
           storeItems.get(key).forEach(s -> System.out.printf("%s%03d%n",s.getName(), s.getId()));
       }
    }
    public List<Item> getOrderList() {
        return orderList;
    }

    public Map<String, NavigableSet<Item>> getStoreItems() {
        return storeItems;
    }

    public static BiConsumer<Item, Item> printDetails = (a, b) -> {
        System.out.println("=======".repeat(5) + a + "=======".repeat(5));
        System.out.println("=======".repeat(5)+ b + "=======".repeat(5));
    };
}
