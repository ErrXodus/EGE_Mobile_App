package com.example.kr;

import android.content.Context;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Session {
    private final Context current_context;

    /**
     * Создание экзаменационной сессии, инициализация контейнера для кнопок
     * @param context контекст текущей активности
     */
    public Session (Context context)
    {
        this.current_context = context;
        session_container = new LinearLayout(current_context);
        session_container.setOrientation(LinearLayout.VERTICAL);
        checkbox_groups_layouts = new ArrayList<>();
        is_ready = false;
    }
    private final LinearLayout session_container;
    public LinearLayout GetSessionLayout()
    {
        return this.session_container;
    }
    private Question[] questions;
    //#region Questions_Access
    /**
     * Установка экзаменационных вопросов сессии
     * @param questions вопросы, считанные из файла
     */
    public void SetQuestions(Question[] questions)
    {
        this.questions = questions;
    }
    //#endregion
    private ArrayList<LinearLayout> checkbox_groups_layouts;
    public List<LinearLayout> GetRadioGroups()
    {
        return this.checkbox_groups_layouts;
    }
    private boolean is_ready;

    /**
     * Запуск размещения всех вопросов на панели
     * Если на панели будет 0 вопросов, то подвести итоги сессии будет нельзя
     */
    public void DrawQuestionsPanel()
    {
        for (Question iter : questions) {
            addQuestionCard(iter);
        }
        if (!checkbox_groups_layouts.isEmpty())
            is_ready = true;
    }

    /**
     * Создание и размещение элементов управления для вопроса
     * @param question вопрос для размещения
     */
    private void addQuestionCard(Question question)
    {
        // Создайте новый объект TextView
        TextView textView = new TextView(current_context);
        textView.setText(question.title);
        //LinearLayout.LayoutParams layout_params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //layout_params.gravity = Gravity.CLIP_VERTICAL;
        LinearLayout layout = new LinearLayout(session_container.getContext());
        // Добавьте TextView в родительский контейнер
        session_container.addView(textView);
        // Создайте и добавьте радиокнопки в группу
        for (String option : question.options) {
            CheckBox checkBox = new CheckBox(current_context);
            checkBox.setText(option);
            //layout.addView(checkBox, layout_params);
            layout.addView(checkBox);
            layout.setOrientation(LinearLayout.VERTICAL);
            checkbox_groups_layouts.add(layout);
        }
        session_container.addView(layout);
    }

    /**
     * Подвести итоги экзаменационной сессии, составление результатов
     * @return массив объектов результатов ответов на вопросы
     * @see QuestionResult
     */
    public QuestionResult[] GetResults()
    {
        if (!is_ready)
            throw new NullPointerException("Нечего проверять, форма сессии не отрисована");
        /*
        * Маппингом является индекс по умолчанию, где каждая radioGruop соответствует своему Question
        * */
        QuestionResult[] session_results = new QuestionResult[questions.length];
        LinearLayout[] checkbox_groups_layouts_array = new LinearLayout[checkbox_groups_layouts.size()];
        checkbox_groups_layouts_array = checkbox_groups_layouts.toArray(checkbox_groups_layouts_array);
        for (int i = 0; i < questions.length; i++)
        {
            ArrayList<String> wrong_answers = new ArrayList<>();
            ArrayList<String> correct_answers = new ArrayList<>();
            for (short j = 0; j < checkbox_groups_layouts_array[i].getChildCount(); j++)
            {
                View view = checkbox_groups_layouts_array[i].getChildAt(j);
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;
                    if (checkBox.isChecked()) {
                        if (Arrays.asList(questions[i].correct_option_numbers).contains(j))
                            correct_answers.add(checkBox.getText().toString());
                        else
                            wrong_answers.add(checkBox.getText().toString());
                    }
                }
            }
            session_results[i].correct_answers = correct_answers.toArray(session_results[i].correct_answers);
            session_results[i].wrong_answers = wrong_answers.toArray(session_results[i].wrong_answers);
            wrong_answers.clear();
            correct_answers.clear();
        }
        return  session_results;
    }
}