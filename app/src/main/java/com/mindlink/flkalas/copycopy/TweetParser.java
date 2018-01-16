package com.mindlink.flkalas.copycopy;

import android.text.Html;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lesenic on 2016-04-15.
 */
public class TweetParser {
    public String parseTweet(String[] tags){
        String strTweet = "";
        //int lenPrev = strTweet.length();
        for(int i = 0; i < tags.length; i++) {
            if (tags[i].contains("Emoji")) {//---------------case emoji
                strTweet += parseEmoji(tags[i]);
                strTweet += parseText(tags[i]);
            } else if (tags[i].contains("://t.co/")) {//----case link
                String[] tagsLink = getLinkTag(tags, i);
                strTweet += " " + parseLink(tagsLink) + " ";
                i += tagsLink.length - 1;
            } else if (tags[i].contains("a href")) {//------case internal tag
                String[] tagsLink = getLinkTag(tags, i);
                strTweet += " " + parseInternalTag(tagsLink)+" ";
                i += tagsLink.length - 1;
            } else if (tags[i].contains(">")) {//------------case plain text
                strTweet += parseText(tags[i]);
            } else {
                strTweet += Html.fromHtml(tags[i]).toString();
            }
        }

        return trimTweet(strTweet);
    }

    public String[] getTweetContext(String[] orgHTML){
        List<String> listTweetTag = new ArrayList<String>();
        int indexOrgTweetStart = 0;
        boolean isTweetFound = false;

        //find target tweet
        for(int i = 0; i < orgHTML.length; i++) {
            if (orgHTML[i].contains("data-tweet-id=\""+orgHTML[0]+"\"")) {
                indexOrgTweetStart = i+1;
                break;
            }
        }
        //find target tweet text
        for(int i = indexOrgTweetStart; i < orgHTML.length; i++) {
            if(orgHTML[i].contains("js-tweet-text-container")) {
                indexOrgTweetStart = i+1;
                isTweetFound = true;
                break;
            }
        }

        if(isTweetFound) {
            for (int i = indexOrgTweetStart; !orgHTML[i].contains("/p>"); i++) {
                listTweetTag.add(orgHTML[i]);
            }
        }

        return listTweetTag.toArray(new String[listTweetTag.size()]);
    }

    public String[] getLinkTag(String[] orgTweet, int indexLinkStart){
        List<String> listLinkTag = new ArrayList<String>();
        //copy utill meeting end
        for(int i = indexLinkStart; !orgTweet[i].contains("/a>"); i++) {
            listLinkTag.add(orgTweet[i]);
        }
        return listLinkTag.toArray(new String[listLinkTag.size()]);
    }

    public String parseLink(String[] linkTags){
        String[] sepaByQuote = linkTags[0].split("\"");
        for (int iterQuote = 0; iterQuote < sepaByQuote.length; iterQuote++) {
            if (sepaByQuote[iterQuote].contains("href=")) {
                return sepaByQuote[iterQuote+1];
            }
        }
        return "";
    }

    public String parseInternalTag(String[] linkTags){
        String tag = "";
        for (int i = 0; i < linkTags.length; i++) {
            tag += parseText(linkTags[i]);
        }
        return tag;
    }

    public String parseText(String orgString){
        String text = "";
        String[] interTags = orgString.split(">");
        if(interTags.length > 1) {
            String[] eachLines = interTags[1].split("\n");
            for(int i = 0; i < eachLines.length; i++) {
                //Log.d("EL", eachLines[i]);
                text += Html.fromHtml(eachLines[i]).toString();
                if(i != eachLines.length-1){
                    text += "\n";
                }
            }
        }
        return text;
    }

    public String parseEmoji(String orgString){
        String[] sepaByQuote = orgString.split("\"");
        for (int iterQuote = 0; iterQuote < sepaByQuote.length; iterQuote++) {
            if (sepaByQuote[iterQuote].contains("alt=")) {
                return sepaByQuote[iterQuote+1];
            }
        }
        return "";
    }

    public String trimTweet(String strOrgTweet){
        String[] eachLines = strOrgTweet.split("\n");
        String trimedTweet = "";
        for(int j = 0; j < eachLines.length; j++) {
            //Log.d("EL", eachLines[i]);
            trimedTweet += eachLines[j].replaceAll("  "," ");
            if(j != eachLines.length-1){
                trimedTweet += "\n";
            }
        }

        return trimedTweet;
    }
}
