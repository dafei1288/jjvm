public class Int {
    public static void main(String[] args) {
        int num1 = plus();
        int num2 = minus();
        int num3 = mult();
        int num4 = div();
        int num5 = rem();
        int num6 = neg();
        System.out.println(num1 + num2 + num3 + num4 + num5 + num6);
    }

    private static int plus() {
        int num1 = 1;
        int num2 = 2;
        int result = num1 + num2;
        System.out.println(result);
        return result;
    }

    private static int minus() {
        int num1 = -1;
        int num2 = 3;
        int num3 = 4;
        int result = num1 - num2 - num3;
        System.out.println(result);
        return result;
    }

    private static int mult() {
        int num1 = 5;
        int num2 = 3;
        int result = num1 * num2;
        System.out.println(result);
        return result;
    }

    private static int div() {
        int num1 = 10;
        int num2 = 5;
        int result = num1 / num2;
        System.out.println(result);
        return result;
    }

    private static int rem() {
        int num1 = 10;
        int num2 = 3;
        int result = num1 % num2;
        System.out.println(result);
        return result;
    }

    private static int neg() {
        int num1 = 3;
        int result = -num1;
        System.out.println(result);
        return result;
    }
}
