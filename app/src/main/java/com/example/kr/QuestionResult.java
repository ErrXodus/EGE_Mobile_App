package com.example.kr;

public class QuestionResult {
    /**
     * Текст вопроса
     */
    public String title;
    /**
     * Ответы пользователя
     */
    public String[] wrong_answers;
    /**
     * Верные ответы
     */
    public String[] correct_answers;
    public QuestionResult(String title, String[] wrong_answers, String[] correct_answers)
    {
        this.title = title;
        this.wrong_answers = wrong_answers;
        this.correct_answers = correct_answers;
    }
}
