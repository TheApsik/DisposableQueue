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
 * @author Dariusz (TheApsik) Madeja
 * @version 1.1
 * @since 1.8
 * @param <E> Type of elements held in this queue
 */
public class DisposableQueue<E> extends AbstractQueue<E> implements Runnable{

    private Node<E> headNode;   // This node contain next item whose will be return.
    private Node<E> newNode;    // This node contain last item node whose was added.
    private int size = 0;       // How many items are contain.

    private Node<E> emptyNodeFirst;   // This node is first node from empty nodes queue.
    private Node<E> emptyNodeLast; // This node is last node from empty nodes queue
    private int sizeEmptyNodes = 0;
    private int minSize = 0;    // This value represent how many nodes should be hold in queue (more = faster [Huge queue]).

    /**
     * <p>maxSize call how many items can be allocate in queue.</p>
     * <p>0 means no limit</p>
     */
    public final int maxSize;

    // Constructors
    public DisposableQueue(){
        maxSize = 0;
    }

    /**
     * @param maxSize The maximum size queue.
     */
    public DisposableQueue(int maxSize){
        this.maxSize = Math.max(0, maxSize);
    }

    /**
     * @param maxSize The maximum size queue.
     * @param minSize The minimal size queue.
     */
    public DisposableQueue(int maxSize, int minSize){
        this.maxSize = Math.max(0, maxSize);
        this.minSize = Math.max(0, minSize);
    }

    // Methods
    private void removeEmptyNode(){
        //System.out.print(sizeEmptyNodes);
        emptyNodeFirst = emptyNodeFirst.nextNode;
        --sizeEmptyNodes;
    }
    private Node<E> getEmptyNode(){
        Node<E> node = emptyNodeFirst;
        removeEmptyNode();
        node.nextNode = null;
        return node;
    }

    private Node<E> getEmptyNodeWithIndex(int index){
        Node <E> emptyNode = emptyNodeFirst;
        for(int i = 1; i<index; ++i)
            emptyNode = emptyNode.nextNode;
        return emptyNode;
    }

    private Node<E> createNode(E e){
        return sizeEmptyNodes > 0 ? getEmptyNode().setElement(e): new Node<>(e);
    }

    private void setNode(E e){
        if(headNode != null) {
            if (headNode.nextNode != null)
                newNode = newNode.nextNode = createNode(e);
            else
                newNode = newNode.nextNode = createNode(e);
        }else
             headNode = newNode = createNode(e);

        ++size;
    }

    private void setEmptyNode(Node<E> node){
        node.element = null;
        node.nextNode = null;

        if(emptyNodeFirst != null) {
            if (emptyNodeFirst.nextNode != null)
                emptyNodeLast = emptyNodeLast.nextNode = node;
            else
                emptyNodeLast = emptyNodeLast.nextNode = node;
        }else
            emptyNodeFirst = emptyNodeLast = node;

        ++sizeEmptyNodes;
    }

    private void removeNode(){
        if(headNode == null)
            return;

        Node<E> node = headNode;

        //Removing
        if(headNode.nextNode == null)
            headNode = newNode = null;
        else
            headNode = headNode.nextNode;

        //Controlling size of empty nodes
        if(sizeEmptyNodes < minSize || sizeEmptyNodes < size/2)
            setEmptyNode(node);

        --size;
    }

    public void setMinSize(int minSize){
        this.minSize = minSize;
    }

    public int getMinSize(){
        return this.minSize;
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
        if(e == null)
            throw new NullPointerException();
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
        E element = headNode == null ? null : headNode.element;
        removeNode();
        return element;
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
        while(size > 0 && minSize > sizeEmptyNodes){
            removeNode();
            //System.out.print("TU");
        }

        if(minSize > 0) {
            if(minSize < sizeEmptyNodes) {
                Node<E> emptyNode = getEmptyNodeWithIndex(minSize);
                emptyNode.nextNode = null;
                emptyNodeLast = emptyNode;
                sizeEmptyNodes = minSize;
            }
        }
        else {
            emptyNodeFirst = emptyNodeLast = null;
            sizeEmptyNodes = 0;
        }

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

        Node<T> setElement(T t){
            element = t;
            return this;
        }

    }
}
