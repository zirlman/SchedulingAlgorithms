package org.unibl.etf.SchedulingAlgorithms;

import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Thread.sleep;

public class Priority {
    private static class Job {
        private int arrivalTime;
        private int id;
        private int priority;
        private int remainingTime;
        private int waitingTime;
        private static int idMaker;

        Job(int arrivalTime, int remainingTime, int priority) {
            this.arrivalTime = arrivalTime;
            this.priority = priority;
            this.remainingTime = remainingTime;
            id = ++idMaker;
        }

        int getArrivalTime() {
            return arrivalTime;
        }

        int getId() {
            return id;
        }

        int getPriority() {
            return priority;
        }

        int getRemainingTime() {
            return remainingTime;
        }

        int getWaitingTime() {
            return waitingTime;
        }

        @Override
        public String toString() {
            return passedTime + " Job " + id + ", remaining time: " + remainingTime;
        }

        void decreaseRemainingTime() {
            --remainingTime;
        }

        void updateWaitingTime() {
            if (passedTime > arrivalTime)
                ++waitingTime;
        }
    }

    private static TreeSet<Job> jobQueue = new TreeSet<>(Comparator
            .comparingInt(Job::getArrivalTime)
            .thenComparing(Job::getPriority)
            .thenComparing(Job::getId));
    private static int passedTime;

    private static void loadJobs(String[] arrivalTime, String[] jobs, String[] priority) {
        for (int i = 0; i < arrivalTime.length; i++)
            jobQueue.add(new Job(Integer.valueOf(arrivalTime[i]), Integer.valueOf(jobs[i]), Integer.valueOf(priority[i])));
        while (!jobQueue.isEmpty()) {
            while (passedTime < jobQueue.first().getArrivalTime()) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++passedTime;
            }
            Job running = jobQueue.pollFirst();
            while (running.getRemainingTime() != 0) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++passedTime;
                running.decreaseRemainingTime();
                jobQueue.forEach(Job::updateWaitingTime);
                System.out.println(running);
            }
            System.out.println(passedTime + " Job " + running.getId() + " finished, total waiting time: " + running.getWaitingTime());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter arrival time (1,2,3,...): ");
        String input1 = scanner.next();
        String[] arrivalTime = input1.split(",");
        System.out.print("Enter working time (1,2,3,...): ");
        String input2 = scanner.next();
        String[] jobs = input2.split(",");
        System.out.print("Enter priority (1,2,3,...): ");
        String input3 = scanner.next();
        String[] priority = input3.split(",");

        if (arrivalTime.length != jobs.length && arrivalTime.length != priority.length)
            System.out.println("Invalid input!");
        else
            loadJobs(arrivalTime, jobs, priority);
    }
}
