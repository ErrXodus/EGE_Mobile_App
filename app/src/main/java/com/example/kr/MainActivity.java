package com.example.kr;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // создание сессии, инициализация
        Session context_session = new Session(this);
        // получение файлового потока
        InputStream stream = getResources().openRawResource(R.raw.testing_text);
        // считывание вопросов
        context_session.SetQuestions(ReadQuestions(stream));
        // отрисовка элементов
        context_session.DrawQuestionsPanel();
        // получение основного слоя
        LinearLayout main_layout = findViewById(R.id.main_layout);
        main_layout.setOrientation(LinearLayout.VERTICAL);
        // установка скролинга
        ScrollView scrollView = new ScrollView(this);
        main_layout.addView(scrollView);
        // получить слой с элементами сессии
        scrollView.addView(context_session.GetSessionLayout());
        // подведение итогов, вызывать по нажатии кнопки "завершить экзамен"
        //QuestionResult[] results = context_session.GetResults();
    }

    Question[] ReadQuestions(InputStream stream)
    {
        /*
        * Структура файла следующая
        QUESTION строка вопроса
        OPTION опция
        OPTION_CORRECT верная опция
        OPTION опция
        QUESTION строка вопроса
        OPTION опция
        OPTION_CORRECT верная опция
        OPTION опция
        * */
        ArrayList<Question> questions = new ArrayList<>();
        short i = 0;
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(stream));
            String line = reader.readLine();

            ArrayList<String> question_options = new ArrayList<>();
            ArrayList<Short> correct_options = new ArrayList<>();
            Question question = new Question();
            while (line != null)
            {
                line = reader.readLine();
                if (line.contains("."))
                {
                    if (!question_options.isEmpty())
                    {
                        String[] tmp_options = new String[question_options.size()];
                        question_options.toArray(tmp_options);
                        question.options = tmp_options;

                        Short[] tmp_correct_option_numbers = new Short[correct_options.size()];
                        correct_options.toArray(tmp_correct_option_numbers);
                        question.correct_option_numbers = tmp_correct_option_numbers;

                        questions.add(question);

                        question_options.clear();
                        correct_options.clear();
                    }
                    question = new Question();
                    question.title = line.substring(line.indexOf(".")+1);
                    i = 0;
                }
                if (line.contains(")"))
                {
                    question_options.add(line.substring(line.indexOf(")")+1));
                    i++;
                }
                if (line.startsWith("Ответ: "))
                {
                    char[] symbols = line.replace("Ответ: ", "").toCharArray();
                    for (char el : symbols)
                        if (Character.isDigit(el))
                            correct_options.add((short) el);
                        else
                            break;
                    i++;
                }
                if (!questions.isEmpty())
                    if(questions.size() == 10)
                        break;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        Question[] result = new Question[questions.size()];
        result = questions.toArray(result);
        return result;
    }
}