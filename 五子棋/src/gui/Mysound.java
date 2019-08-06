package gui;

import java.io.*;

import java.applet.AudioClip;
import java.io.*;
import java.applet.Applet;
import java.awt.Frame;
import java.net.MalformedURLException;
import java.net.URL;

import javax.sound.sampled.AudioInputStream;

public class Mysound
{
    @SuppressWarnings("deprecation")
    public void playMusic()
    {
        try
        {
            URL cb;
            File f = new File("src\\img\\music.wav");
            cb = f.toURL();
            AudioClip as = Applet.newAudioClip(cb);
            as.loop();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
    public void playMusicwin()
    {
        try
        {
            URL cb;
            File f = new File("src\\img\\win.wav");
            cb = f.toURL();
            AudioClip as = Applet.newAudioClip(cb);
            as.play();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
    public void playMusicclose()
    {
        try
        {
            URL cb;
            File f = new File("src\\img\\win.wav");
            cb = f.toURL();
            AudioClip as = Applet.newAudioClip(cb);
            as.stop();
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }
    public void playMusiclz()
    {
        try
        {
            URL cb;
            File f = new File("src\\img\\luozi.wav");
            cb = f.toURL();
            AudioClip as = Applet.newAudioClip(cb);
            as.play();

        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String args[])
    {
      
       
    }
}
