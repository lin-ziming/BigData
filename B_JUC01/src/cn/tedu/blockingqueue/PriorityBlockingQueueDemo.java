package cn.tedu.blockingqueue;

import java.util.concurrent.PriorityBlockingQueue;

public class PriorityBlockingQueueDemo {

    public static void main(String[] args) throws InterruptedException {

        // PriorityBlockingQueue<String> queue = new PriorityBlockingQueue<>();
        // queue.put("Helen");
        // queue.put("Frank");
        // queue.put("Iran");
        // queue.put("Mary");
        // queue.put("Simon");
        // queue.put("Bruce");
        // queue.put("Alex");

        // 可以给队列单独指定比较规则
        PriorityBlockingQueue<Student> queue =
                new PriorityBlockingQueue<>(7,
                        (s1, s2) -> s1.getAge() - s2.getAge());
        queue.put(new Student("John", 18, 75));
        queue.put(new Student("David", 16, 60));
        queue.put(new Student("Bob", 19, 61));
        queue.put(new Student("Eden", 17, 65));
        queue.put(new Student("Adair", 18, 59));
        queue.put(new Student("Grace", 19, 82));
        queue.put(new Student("Cindy", 21, 79));

        // for (int i = 0; i < 7; i++) {
        //     System.out.println(queue.take());
        // }

        for (Student s : queue) {
            System.out.println(s);
        }

    }

}

class Student implements Comparable<Student> {

    private String name;
    private int age;
    private int score;

    public Student(String name, int age, int score) {
        this.name = name;
        this.age = age;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "Student{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", score=" + score +
                '}';
    }

    // 按照分数进行降序排序
    // this - o -> 升序
    // o - this -> 降序
    @Override
    public int compareTo(Student o) {
        return o.score - this.score;
    }
}
