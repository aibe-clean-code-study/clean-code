public class before {
    public static void main(String[] args) {
        boolean[] isSelfNumber = new boolean[10001];
        for (int i = 1; i <= 10000; i++) {
            int next = getNext(i);
            while (next <= 10000 && !isSelfNumber[next]) {
                isSelfNumber[next] = true;
                next = getNext(next);
            }
        }

        for (int i = 1; i <= 10000; i++) {
            if (!isSelfNumber[i]) {
                System.out.println(i);
            }
        }
    }

    public static int getNext(int n) {
        int next = n;
        while (n > 0) {
            next += n % 10;
            n /= 10;
        }
        return next;
    }
}
