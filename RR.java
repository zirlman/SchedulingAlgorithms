package org.unibl.etf.SchedulingAlgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Thread.sleep;

public class RR {
    private static class Job {
        private int arrivalTime;
        private int remainingTime;
        private int waitingTime;
        private int workingTime;


        private int id;
        private static int idMaker;

        Job(int arrivalTime, int remainingTime) {
            this.arrivalTime = arrivalTime;
            this.remainingTime = remainingTime;
            id = ++idMaker;
        }

        int getArrivalTime() {
            return arrivalTime;
        }

        int getRemainingTime() {
            return remainingTime;
        }

        int getWaitingTime() {
            return waitingTime;
        }

        int getWorkingTime() {
            return workingTime;
        }

        int getId() {
            return id;
        }

        @Override
        public String toString() {
            return passedTime + " Job " + id + ", remaining time: " + remainingTime;
        }

        void decreaseRemainingTime() {
            --remainingTime;
        }

        void increaseWorkingTime() {
            ++workingTime;
        }

        void updateWaitingTime() {
            if (passedTime > arrivalTime)
                ++waitingTime;
        }
    }

    private static TreeSet<Job> jobQueue = new TreeSet<>(Comparator
            .comparingInt(Job::getArrivalTime)
            .thenComparing(Job::getId));
    private static ArrayList<Job> activeJobs = new ArrayList<>();
    private static int passedTime;
    private static final int quantum = 2;

    private static void loadJobs(String[] arrivalTime, String[] jobs) {
        for (int i = 0; i < arrivalTime.length; i++)
            jobQueue.add(new Job(Integer.valueOf(arrivalTime[i]), Integer.valueOf(jobs[i])));
        while (!jobQueue.isEmpty()) {
            while (passedTime < jobQueue.first().getArrivalTime()) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++passedTime;
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
                    ++passedTime;
                    running.decreaseRemainingTime();
                    running.increaseWorkingTime();
                    System.out.println(running);
                    jobQueue.forEach(Job::updateWaitingTime);
                    activeJobs.forEach(Job::updateWaitingTime);
                    updateActiveJobs();
                    if (running.getWorkingTime() % quantum == 0)
                        break;
                }
                if (running.getRemainingTime() == 0)
                    System.out.println(passedTime + " Job " + running.getId() + " finished, total waiting time: " + running.getWaitingTime());
                else
                    activeJobs.add(running);
            }
        }
    }

    private static void updateActiveJobs() {
        while (!jobQueue.isEmpty() && passedTime >= jobQueue.first().getArrivalTime())
            activeJobs.add(jobQueue.pollFirst());
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter arrival time for each job (1,2,3,...): ");
        String input1 = scanner.next();
        String[] arrivalTime = input1.split(",");
        System.out.print("Enter working time for each job (1,2,3,...): ");
        String input2 = scanner.next();
        String[] jobs = input2.split(",");

        if (arrivalTime.length != jobs.length)
            System.out.println("Invalid input (arrivalTime.length != jobs.length) !");
        else
            loadJobs(arrivalTime, jobs);
    }
}
