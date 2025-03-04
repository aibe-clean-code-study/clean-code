public class after {
    public static void main(String[] args) {
        int MAX_ARRAY_SIZE = 10001;
        boolean[] isSelfNumber = new boolean[MAX_ARRAY_SIZE];
        for (int INDEX = 1; INDEX <= MAX_ARRAY_SIZE - 1; INDEX++) {
            int next = getNext(INDEX);
            while (next <= MAX_ARRAY_SIZE && !isSelfNumber[next]) {
                isSelfNumber[next] = true;
                next = getNext(next);
            }
        }

        for (int INDEX = 1; INDEX <= MAX_ARRAY_SIZE - 1; INDEX++) {
            if (!isSelfNumber[INDEX]) {
                System.out.println(INDEX);
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
