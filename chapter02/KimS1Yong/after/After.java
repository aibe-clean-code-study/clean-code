package chapter02.KimS1Yong.after;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

// 다리를 지나는 트럭 (큐 이용) 문제
public class After {
    public static void main(String[] args) {
        //test code
        System.out.println(solution(2, 10, new int[]{7,4,5,6}));
        System.out.println(solution(100, 100, new int[]{10}));
        System.out.println(solution(100, 100, new int[]{10,10,10,10,10,10,10,10,10,10}));
    }

    public static int solution(int bridge_length, int weight, int[] truck_weights) {

        Deque<Integer> bridge = new ArrayDeque<>();

        final int EMPTY_SPACE = 0;
        for (int i=0; i<bridge_length-1; i++) { //다리 길이 만큼 공간 채워두기
            bridge.offer(EMPTY_SPACE);
        }

        int currentBridgeWeight=truck_weights[0];
        bridge.offer(currentBridgeWeight);

        int totalTime=1;
        int truckIdx=1;

        while (!bridge.isEmpty()) {
            totalTime++;

            int removedTruckWeight = bridge.poll();
            currentBridgeWeight-=removedTruckWeight;

            if (truckIdx < truck_weights.length) {
                int nextCurrentBridgeWeight=currentBridgeWeight+truck_weights[truckIdx];
                if(nextCurrentBridgeWeight <= weight) {
                    currentBridgeWeight+=truck_weights[truckIdx];
                    bridge.offer(truck_weights[truckIdx]);
                    truckIdx++;
                    continue;
                }

                bridge.offer(EMPTY_SPACE);
            }
        }
        return totalTime;
    }
}
