package org.unibl.etf.SchedulingAlgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Thread.sleep;

public class PriorityAging {
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
            return pasedTime + " Job " + id + ", remaining time: " + remainingTime;
        }

        void decreaseRemainingTime() {
            --remainingTime;
        }

        void updatePriority() {
            if (priority > 1 && pasedTime > arrivalTime)
                --priority;
        }

        void updateWaitingTime() {
            if (pasedTime > arrivalTime)
                ++waitingTime;
        }
    }

    private static TreeSet<Job> jobQeue = new TreeSet<>(Comparator
            .comparingInt(Job::getArrivalTime)
            .thenComparing(Job::getPriority)
            .thenComparing(Job::getId));
    private static ArrayList<Job> activeJobs = new ArrayList<>();
    private static int pasedTime;

    private static void loadJobs(String[] arrivalTime, String[] jobs, String[] priority) {
        for (int i = 0; i < arrivalTime.length; i++)
            jobQeue.add(new Job(Integer.valueOf(arrivalTime[i]), Integer.valueOf(jobs[i]), Integer.valueOf(priority[i])));
        while (!jobQeue.isEmpty()) {
            while (pasedTime < jobQeue.first().getArrivalTime()) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++pasedTime;
            }
            updateActiveJobs();
            while (!activeJobs.isEmpty()) {
                Job running = activeJobs.remove(0);
                while (running.getRemainingTime() != 0) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ++pasedTime;
                    running.decreaseRemainingTime();
                    System.out.println(running);
//                    jobQeue.forEach(c -> {
//                        c.updateWaitingTime();
//                        c.updatePriority();
//                    });
                    // Svim aktivnim procesima je potrebno uvecati vrijeme cekanja i smanjiti prioritet
                    activeJobs.forEach(c -> {
                        c.updateWaitingTime();
                        c.updatePriority();
                    });
                    updateActiveJobs();
                    if (!activeJobs.isEmpty() && running.getPriority() > activeJobs.get(0).getPriority()) {
                        activeJobs.add(running);
                        running = activeJobs.remove(0);
                        activeJobs.sort(Comparator.comparingInt(Job::getPriority).thenComparing(Job::getId));
                    }
                }
                System.out.println(pasedTime + " Job " + running.getId() + " finished, total waiting time: " + running.getWaitingTime());
            }
        }
    }

    private static void updateActiveJobs() {
        while (!jobQeue.isEmpty() && pasedTime >= jobQeue.first().getArrivalTime())
            activeJobs.add(jobQeue.pollFirst());
        activeJobs.sort(Comparator.comparingInt(Job::getPriority).thenComparing(Job::getId));
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
