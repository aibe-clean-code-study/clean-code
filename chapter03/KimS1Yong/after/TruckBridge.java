package chapter03.KimS1Yong.after;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;

// 다리를 지나는 트럭 (큐 이용) 문제
import java.util.ArrayDeque;
import java.util.Deque;

// 다리를 지나는 트럭 문제 해결
public class TruckBridge {
    public static void main(String[] args) {
        System.out.println(solution(2, 10, new int[]{7, 4, 5, 6}));
        System.out.println(solution(100, 100, new int[]{10}));
        System.out.println(solution(100, 100, new int[]{10, 10, 10, 10, 10, 10, 10, 10, 10, 10}));
    }

    public static int solution(int bridgeLength, int weight, int[] truckWeights) {
        Deque<Integer> bridge = initializeBridge(bridgeLength);

        int currentBridgeWeight = truckWeights[0];
        bridge.offer(currentBridgeWeight);

        int totalTime = 1;
        int truckIdx = 1;

        while (!bridge.isEmpty()) {
            totalTime++;
            currentBridgeWeight -= bridge.poll();

            if (truckIdx < truckWeights.length) {
                if (canTruckEnter(currentBridgeWeight, truckWeights[truckIdx], weight)) {
                    currentBridgeWeight += truckWeights[truckIdx];
                    bridge.offer(truckWeights[truckIdx]);
                    truckIdx++;
                    continue;
                }
                bridge.offer(0);

            }
        }

        return totalTime;
    }

    private static Deque<Integer> initializeBridge(int bridgeLength) {
        Deque<Integer> bridge = new ArrayDeque<>();
        for (int i = 0; i < bridgeLength - 1; i++) {
            bridge.offer(0);
        }
        return bridge;
    }

    private static boolean canTruckEnter(int currentBridgeWeight, int truckWeight, int maxWeight) {
        return (currentBridgeWeight + truckWeight) <= maxWeight;
    }
}
