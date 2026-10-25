record Order (String orderId, String orderType, int qty) {

    public String toString(){
        return "%-15s %-20s (%s)".formatted(orderId, orderType, qty);
    }
}

public class Item {

    private final int id;
    private final String name;

    public Item(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Item(Item item){
        this.id = item.id;
        this.name = item.name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Item item)) return false;

        if (id != item.id) return false;
        return name.equals(item.name);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        return result;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "%s0%03d".formatted(getName(), getId());
    }
}
