package chapter02.KimS1Yong.before;

import java.util.ArrayDeque;
import java.util.Deque;

// 다리를 지나는 트럭 (큐 이용) 문제
public class Before {
    public static void main(String[] args) {
        //test code
        System.out.println(solution(2, 10, new int[]{7,4,5,6}));
        System.out.println(solution(100, 100, new int[]{10}));
        System.out.println(solution(100, 100, new int[]{10,10,10,10,10,10,10,10,10,10}));
    }

    public static int solution(int bridge_length, int weight, int[] truck_weights) {

        Deque<Integer> queue = new ArrayDeque<>(); // 큐가 다리 역할

        // 다리의 비어 있는 공간은 0으로 채워둠 (0번째 트럭은 바로 올림)
        for (int i=0; i<bridge_length-1; i++) {
            queue.offer(0);
        }

        int currentW=truck_weights[0]; // 현재 다리의 무게
        queue.offer(currentW);

        int time=1; // 시간 (정답)
        int idx=1; // 트럭의 index

        while (!queue.isEmpty()) {
            time++;

            int removed = queue.poll(); // 빠져나간 트럭 무게
            currentW-=removed;

            if (idx < truck_weights.length) { // 올라갈 트럭 남아있으면
                if(currentW+truck_weights[idx] <= weight) {
                    // 다리 무게 여유 있으면
                    currentW+=truck_weights[idx];
                    queue.offer(truck_weights[idx]);
                    idx++;
                    continue;
                }
                // 다리 무게 여유 없으면 빈 공간 나태는 0을 다리에 올림
                queue.offer(0);
            }
        }
        return time;
    }
}
