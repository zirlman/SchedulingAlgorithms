package org.unibl.etf.SchedulingAlgorithms;

import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Thread.sleep;

public class FCFS {
    private static class Job {
        private int arrivalTime;
        private int waitingTime;
        private int remainingTime;
        private int id;
        private static int idMaker;

        public Job(int arrivalTime, int remainingTime) {
            this.arrivalTime = arrivalTime;
            this.remainingTime = remainingTime;
            id = ++idMaker;
        }

        public int getArrivalTime() {
            return arrivalTime;
        }

        public int getWaitingTime() {
            return waitingTime;
        }

        public int getRemainingTime() {
            return remainingTime;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Job " + id + ", remaining time: " + remainingTime;
        }

        void decreaseRemainingTime() {
            --remainingTime;
        }

        void increaseWaitingTime() {
            ++waitingTime;
        }

        void updateWaitingTime() {
            if (passedTime > arrivalTime)
                ++waitingTime;
        }
    }

    private static TreeSet<Job> jobQueue = new TreeSet<>(Comparator.comparingInt(Job::getArrivalTime).thenComparing(Job::getId));
    private static int passedTime;

    private static void loadJobs(String[] arrivalTime, String[] jobs) {
        for (int i = 0; i < arrivalTime.length; i++)
            jobQueue.add(new Job(Integer.valueOf(arrivalTime[i]), Integer.valueOf(jobs[i])));
        while (!jobQueue.isEmpty()) {
            while (passedTime++ < jobQueue.first().getArrivalTime()) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
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
                System.out.println(running);
                jobQueue.forEach(Job::updateWaitingTime);
            }
            System.out.println("Job " + running.getId() + " finished, total waiting time: " + running.getWaitingTime());
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter arrival time for each job (1,2,3,4,5...): ");
        String input2 = scanner.next();
        String[] arrivalTime = input2.split(",");
        System.out.print("Enter working time for each job (1,2,3,4,5...): ");
        String input1 = scanner.next();
        String[] jobs = input1.split(",");

        if (arrivalTime.length != jobs.length)
            System.out.println("Invalid input (arrivalTime.length != jobs.length)!");
        else
            loadJobs(arrivalTime, jobs);
    }
}
