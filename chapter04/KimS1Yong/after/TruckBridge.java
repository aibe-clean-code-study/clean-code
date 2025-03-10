package chapter04.KimS1Yong.after;

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
        // 각 트럭이 다리를 지나는데 걸리는 시간 = bridgeLength
        // bridgeLength 만큼 빈 공간을 채워서 각 트럭이 다리를 지나는 시간 계산하기
        // 트럭이 다리를 지나는 동안의 시간을 bridge에서 빈 공간을 poll하는 시간으로 대체

        Deque<Integer> bridge = initializeBridge(bridgeLength);

        // 처음 트럭은 다리에 그냥 들어간다고 생각
        int currentBridgeWeight = truckWeights[0];
        bridge.offer(currentBridgeWeight);

        int totalTime = 1;
        int truckIdx = 1;

        while (!bridge.isEmpty()) {
            totalTime++;
            // 다리에서 truck이든 빈 공간이든 빠져나감
            currentBridgeWeight -= bridge.poll();

            if (truckIdx < truckWeights.length) {
                if (canTruckEnter(currentBridgeWeight, truckWeights[truckIdx], weight)) {
                    currentBridgeWeight += truckWeights[truckIdx];
                    bridge.offer(truckWeights[truckIdx]);
                    truckIdx++;
                    continue;
                }
                // 다리에 들어가는 트럭 없으면 빈공간 넣기
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
