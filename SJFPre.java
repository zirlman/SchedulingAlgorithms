package org.unibl.etf.SchedulingAlgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Scanner;
import java.util.TreeSet;

import static java.lang.Thread.sleep;

public class SJFPre {
    private static class Job {
        private int arrivalTime;
        private int waitingTime;
        private int remainingTime;
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

        int getWaitingTime() {
            return waitingTime;
        }

        int getRemainingTime() {
            return remainingTime;
        }

        int getId() {
            return id;
        }

        void updateWaitingTime() {
            if (passedTime > arrivalTime)
                ++waitingTime;
        }

        void decreaseWorkingTime() {
            --remainingTime;
        }

        @Override
        public String toString() {
            return "Job " + id + ", remaining time: " + remainingTime;
        }
    }

    private static TreeSet<Job> jobsQueue = new TreeSet<>(Comparator
            .comparingInt(Job::getArrivalTime)
            .thenComparing(Job::getRemainingTime)
            .thenComparing(Job::getId));
    private static ArrayList<Job> availableJobs = new ArrayList<>();
    private static int passedTime;

    private static void loadJobs(String[] arrivalTime, String[] jobs) {
        for (int i = 0; i < arrivalTime.length; i++)
            jobsQueue.add(new Job(Integer.valueOf(arrivalTime[i]), Integer.valueOf(jobs[i])));
        while (!jobsQueue.isEmpty()) {
            // CPU je u idle stanju dok ne dodje prvi proces
            while (passedTime < jobsQueue.first().getArrivalTime()) {
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ++passedTime;
            }
            // dodaje sve procese koji su spremni u availableJobs i sortira ih po vremenu izvrsavanja
            updateAvailableJobs();
            while (!availableJobs.isEmpty()) {
                Job running = availableJobs.remove(0);
                while (running.getRemainingTime() != 0) {
                    try {
                        sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ++passedTime;
                    running.decreaseWorkingTime();
                    System.out.println(running);
                    availableJobs.forEach(Job::updateWaitingTime);
                    jobsQueue.forEach(Job::updateWaitingTime);
                    // Ukoliko se za vrijeme rada probudio proces sa kracim vremenom izvrsavanja, dodaje se u availableJobs
                    updateAvailableJobs();
                    // Promjena konteksta
                    if (!availableJobs.isEmpty() && running.getRemainingTime() > availableJobs.get(0).getRemainingTime()) {
                        availableJobs.add(running);
                        running = availableJobs.remove(0);
                        availableJobs.sort(Comparator.comparingInt(Job::getRemainingTime));
                    }
                }
                System.out.println("Job " + running.getId() + " finished, total waiting time: " + running.getWaitingTime());
            }
        }
    }

    private static void updateAvailableJobs() {
        int oldSize = jobsQueue.size();
        while (!jobsQueue.isEmpty() && passedTime == jobsQueue.first().getArrivalTime())
            availableJobs.add(jobsQueue.pollFirst());
        int newSize = jobsQueue.size();
        if (oldSize != newSize)
            availableJobs.sort(Comparator.comparingInt(Job::getRemainingTime));
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
            System.out.println("Invalid input (arrivalTime.length != jobs.length)");
        else
            loadJobs(arrivalTime, jobs);
    }
}
