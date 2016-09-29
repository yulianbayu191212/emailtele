package edu.kulikov.email2telegram.email.util;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrey Kulikov (ankulikov)
 * @date 20.09.2016
 */
public class HtmlExtractor {
    public static String parseForMarkdown(String input) {
        Document doc = Jsoup.parse(input);
        final StringBuilder accum = new StringBuilder();
        final Map<Integer,String> hrefs = new HashMap<>();
        final Map<Integer, String> linebreaks = new HashMap<>();
        (new NodeTraversor(new NodeVisitor() {
            public void head(Node node, int depth) {
                String href = hrefs.get(depth);
                if (href != null) {
                    accum.append("](").append(href).append(")");
                    hrefs.remove(depth);
                }
                String linebreak = linebreaks.get(depth);
                if (linebreak != null) {
                    accum.append(linebreak);
                    linebreaks.remove(depth);
                }
                if(node instanceof TextNode) {

                    TextNode element = (TextNode)node;
                    appendNormalisedText(accum, element);
                } else if(node instanceof Element) {
                    if (node.nodeName().equals("a")) {
                        hrefs.put(depth,node.attr("href"));
                        accum.append("[");
                    }
                    if (node.nodeName().equals("p")) {
                        linebreaks.put(depth, "\n");
                    }
                    Element element1 = (Element)node;
                    if(accum.length() > 0 && (element1.isBlock() || element1.tag().getName().equals("br")) && !lastCharIsWhitespace(accum)) {
                        accum.append(" ");
                    }
                }

            }

            public void tail(Node node, int depth) {
            }
        })).traverse(doc);
        if (!hrefs.isEmpty()) {
            for (String href : hrefs.values()) {
                accum.append("](").append(href).append(")");
            }
            hrefs.clear();
        }
        if (!linebreaks .isEmpty()) {
            linebreaks.values().forEach(accum::append);
            linebreaks.clear();
        }
        return accum.toString().trim();
    }


    private static boolean lastCharIsWhitespace(StringBuilder sb) {
        return sb.length() != 0 && sb.charAt(sb.length() - 1) == 32;
    }

    private static void appendNormalisedText(StringBuilder accum, TextNode textNode) {
        String text = textNode.getWholeText();
        if(preserveWhitespace(textNode.parentNode())) {
            accum.append(text);
        } else {
            StringUtil.appendNormalisedWhitespace(accum, text, lastCharIsWhitespace(accum));
        }
    }

    private static boolean preserveWhitespace(Node node) {
        if(node != null && node instanceof Element) {
            Element element = (Element)node;
            return element.tag().preserveWhitespace() || element.parent() != null && element.parent().tag().preserveWhitespace();
        } else {
            return false;
        }
    }
}
