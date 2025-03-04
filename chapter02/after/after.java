public class after {
    public static void main(String[] args) {
        int MAX_ARRAY_SIZE = 10001;
        boolean[] isSelfNumber = new boolean[MAX_ARRAY_SIZE];
        for (int index = 1; index <= MAX_ARRAY_SIZE - 1; index++) {
            int next = getNext(index);
            while (next <= MAX_ARRAY_SIZE && !isSelfNumber[next]) {
                isSelfNumber[next] = true;
                next = getNext(next);
            }
        }

        for (int index = 1; index <= MAX_ARRAY_SIZE - 1; index++) {
            if (!isSelfNumber[index]) {
                System.out.println(index);
            }
        }
    }

    public static int getNext(int number) {
        int nextNumber = number;
        while (number > 0) {
            nextNumber += number % 10;
            number /= 10;
        }
        return nextNumber;
    }
}
