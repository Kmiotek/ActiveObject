package syncBuffer;

import basic.ThreadStatisticCollector;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Buffer3Lock implements IBuffer{


    int maxPortionSize;
    private int actualLength = 0;
    private final int maxLength;
    int operationLimit = 1000000;
    private boolean withStatistic;


    List<Integer> data = new LinkedList<Integer>();
    ReentrantLock dataLock = new ReentrantLock();
    ReentrantLock producerLock = new ReentrantLock();
    ReentrantLock consumerLock = new ReentrantLock();

    Condition producer = dataLock.newCondition();
    Condition consumer = dataLock.newCondition();

    public Buffer3Lock(int size){
        this.maxLength = size*2;
        this.actualLength = 0;
    }


    public void put( int pID , int size ) throws InterruptedException{
        producerLock.lockInterruptibly();
        try{
            dataLock.lockInterruptibly();
            try {

                while (maxLength - actualLength < size) {
                    ThreadStatisticCollector.instance.noteWait(pID);
                    this.producer.await();
                }
                while (size > 0) {
                    this.data.add(size);
                    this.actualLength++;
                    --size;
                }

                //System.out.println("Buffer now contain:" + this.actualLength);

                ThreadStatisticCollector.instance.noteAction(pID);
                this.consumer.signal();
            }
            finally{

                dataLock.unlock();
            }

        }finally{

            producerLock.unlock();
        }

    }

    public List<Integer> get(int pID, int size) throws InterruptedException{
        consumerLock.lockInterruptibly();
        try{
            dataLock.lockInterruptibly();
            try{
                while( actualLength < size){
                    ThreadStatisticCollector.instance.noteWait(pID);
                    this.consumer.await();
                }

                List<Integer> dataToReturn  = new LinkedList<>();

                while(size --> 0){
                    dataToReturn.add(this.data.remove(0));
                    this.actualLength--;
                }

                //System.out.println("Buffer now contain:" + this.actualLength);

                ThreadStatisticCollector.instance.noteAction(pID);
                this.producer.signal();

            } finally {
                dataLock.unlock();
            }

        }finally {
            consumerLock.unlock();
        }

        return data;
    }



}
