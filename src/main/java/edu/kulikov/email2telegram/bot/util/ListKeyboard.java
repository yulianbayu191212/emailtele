package edu.kulikov.email2telegram.bot.util;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 06.09.2016
 */
public class ListKeyboard extends ReplyKeyboardMarkup {
    public ListKeyboard(List<String> list, int rowSize) {
        fillKeyboard(list, rowSize);
    }

    public ListKeyboard(List<String> list) {
       fillKeyboardRowSizeIsOne(list);
    }

    private void fillKeyboardRowSizeIsOne(List<String> list) {
        List<KeyboardRow> allRows = new ArrayList<>();
        for (String value : list) {
            KeyboardRow currentRow = new KeyboardRow();
            currentRow.add(value);
            allRows.add(currentRow);
        }
        setKeyboard(allRows);
    }

    private void fillKeyboard(List<String> list, int rowSize) {
        List<KeyboardRow> allRows = new ArrayList<>();
        KeyboardRow currentRow = null;
        for (int i = 0; i < list.size(); i++) {
            if (i % rowSize == 0) {
                //before creating new row add current to the list
                if (currentRow != null) allRows.add(currentRow);
                currentRow = new KeyboardRow();
            }
            currentRow.add(list.get(i));
            if (i == list.size() - 1) {
                allRows.add(currentRow);
            }
        }
        setKeyboard(allRows);
    }
}
