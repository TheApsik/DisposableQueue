package net.apsik.collections;

import com.sun.istack.internal.NotNull;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;

/**
 * <p>This class give you ability to storage object whose designed to single
 * operation like: reformatting date, handle events or mediate between structures of data.</p>
 *
 * <p>The main reason to create this queue is storage huge amounts of data
 * whose have to be handled, but after that, they don't longer needed.</p>
 *
 * @author Dariusz Madeja
 * @version 1.0
 * @since 1.8
 * @param <E> Type of elements held in this queue
 */

public class DisposableQueue<E> extends AbstractQueue<E> implements Runnable{

    private Node<E> headNode; //This node is next node whose will be return.
    private Node<E> newNode; //This node is last node whose was added.
    private int size = 0; // How many items are contain.

    /**
     * <p>maxSize call how many items can be allocate in queue.</p>
     * <p>0 means no limit</p>
     */
    public final int maxSize;

    // Constructors
    public DisposableQueue(){
        maxSize = 0;
    }
    public DisposableQueue(int maxSize){
        this.maxSize = maxSize;
    }


    private void setNode(E e){
        if(headNode != null) {
            if (headNode.nextNode != null)
                newNode = newNode.nextNode = new Node<>(e);
            else
                headNode.nextNode = newNode = newNode.nextNode = new Node<>(e);
        }else
             headNode = newNode = new Node<>(e);

        ++size;
    }

    private Node<E> getNode(){
        Node<E> node = headNode;
        removeNode();
        return node;
    }

    private void removeNode(){
        if(headNode == null)
            return;

        if(headNode.nextNode == null)
            headNode = newNode = null;
        else
            headNode = headNode.nextNode;

        --size;
    }

    // Hmm... should I let you implement interface Runnable user? In your subclass?
    @Override
    public void run(){
        throw new UnsupportedOperationException("Method Runnable.run() wasn't Override!");
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            @Override
            public boolean hasNext() {
                return headNode != null;
            }

            @Override
            public E next() {
                return DisposableQueue.super.remove();
            }
        };
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean offer(@NotNull E e) {
        if(maxSize != 0 && size >= maxSize)
            return false;

        setNode(e);
        return true;
    }

    /**
     * @return E element and remove it from head of queue
     */
    @Override
    public E poll() {
        Node<E> node = getNode();
        return node == null ? null : node.element;
    }

    /**
     * @return E element from head of queue
     */
    @Override
    public E peek() {
        return headNode == null ? null : headNode.element;
    }

    @Override
    public void clear(){
        newNode = headNode = null;
        size = 0;
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException("This method hasn't any sense in this queue");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("This method hasn't any sense in this queue");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("This method hasn't any sense in this queue");
    }

    @Override
    public String toString(){
        //Original code {AbstractCollection}
        if(headNode == null)
            return "[]";

        StringBuilder sb = new StringBuilder();
        sb.append('[');

        Node<E> node = headNode;
        while(node != null){
            sb.append(node.element == this ? "(this Collection)" : node.element)
                .append(',')
                .append(' ');

            node = node.nextNode;
        }

        return sb.append(']').toString();
    }

    private class Node<T>{
        T element;
        Node<T> nextNode = null;

        Node(T element){
            this.element = element;
        }

    }
}
