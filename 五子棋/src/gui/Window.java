package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

public class Window
{

    public static void main(String[] args)
    {
        MyFrame mf = new MyFrame();

    }

}

class MyFrame extends JFrame
{

    // 存棋子的数组
    private int[][] chess = new int[19][19];
    // 用于悔棋的数组
    private int[][] chessBox = new int[19][19];
    // true表示白方，false表示黑方。
    boolean flag = false;
    // 是否设置了时间
    boolean isSetTime = false;
    // 初始化倒计时的时间
    int time;
    // 初始化 倒计时点击时间
    int ptime;
    // 让线程终止true表示线程运行，false表示线程结束。
    boolean isThread = false;
    // 判断游戏是否开始false表示没有开始，true表示开始。
    boolean isStart = false;
    // 判断游戏是否在运行中false表示没有运行，true表示运行。
    boolean isRun = false;
    // 走的步数
    int stepNum;
    // FiveChess里面含有判断输赢的方法。
    FiveChess fc = new FiveChess();
    // 播放落子的声音。
    Mysound ms = new Mysound();

    // 构造方法
    public MyFrame()
    {
        super(" 五子棋");
        setResizable(false);
        setSize(805, 785);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mouseClickEvent();
        setVisible(true);

    }

    // 鼠标点击
    public void mouseClickEvent()
    {

        addMouseListener(new MouseAdapter()
        {
            public void mouseClicked(MouseEvent e)
            {
                System.out.println(e.getX() + "," + e.getY());
                // 没有设置时间不能运行。
                if (e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 114 && e.getY() <= 153 && (isSetTime == false))
                {
                    JOptionPane.showMessageDialog(null, "游戏开始之前请先设置时间，否则不能点击开始", "按照规矩来",
                            JOptionPane.INFORMATION_MESSAGE);
                }
                // 开始游戏，游戏没有运行。
                if (e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 114 && e.getY() <= 153 && (isRun == false)
                        && isSetTime == true)
                {
                    start();
                }
                // 游戏开始不能点击开始游戏。
                if (e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 114 && e.getY() <= 153 && (isRun == true))
                {
                    JOptionPane.showMessageDialog(null, "游戏已经开始，不能点击开始", "按照规矩来", JOptionPane.INFORMATION_MESSAGE);
                }

                // 游戏设置时间。
                if (e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 189 && e.getY() <= 235)
                {
                    // 游戏没有运行才能设置游戏。
                    if (!(MyFrame.this.isRun))
                    {

                        while (true)
                        {
                            String num = JOptionPane.showInputDialog(null, "请选择时间", "时间设置(秒)",
                                    JOptionPane.INFORMATION_MESSAGE);
                            try
                            {
                                if (Integer.parseInt(num) >= 5 && Integer.parseInt(num) <= 180)
                                {
                                    time = Integer.parseInt(num);
                                    ptime = Integer.parseInt(num);
                                    // 表示已经设置了时间
                                    isSetTime = true;
                                    repaint();
                                    break;
                                }
                            } catch (Exception exp)
                            {
                                JOptionPane.showMessageDialog(null, "你只能输入5-180之间的数字", "按照规矩来",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        }

                    }
                    // 游戏开始的时候不能修改时间。
                    else
                    {
                        JOptionPane.showMessageDialog(null, "游戏已经开始，不能修改时间", "按照规矩来", JOptionPane.INFORMATION_MESSAGE);
                    }

                }

                // 和局
                if (stepNum > 100)
                {
                    JOptionPane.showMessageDialog(null, "旗鼓相当，再接再厉", "和局", JOptionPane.INFORMATION_MESSAGE);
                    // 结束线程
                    isThread = true;
                    // 改变运行状态。
                    isRun = false;
                    Object[] options = { "继续游戏", "退出游戏" };
                    int response = JOptionPane.showOptionDialog(null, "", "继续努力", JOptionPane.YES_OPTION,
                            JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                    if (response == 0)
                    {
                        // isRun = false;
                        isStart = false;
                        flag = false;
                        start();
                    }
                    // 选择退出游戏
                    else
                    {
                        System.exit(0);
                    }
                }

                // 悔棋
                if (e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 357 && e.getY() <= 395 && isRun == true)
                {
                    // 调用悔棋的方法。
                    regret();
                }

                // 认输
                if (e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 434 && e.getY() <= 469&&stepNum>3)
                {
                    boolean flg = true;
                    // 游戏没有运行不能点击认输。
                    if (isRun)
                    {
                        int result = JOptionPane.showConfirmDialog(null, "是否要认输");
                        if (result == 0)
                        {
                            isThread = true;
                            if (!(flag))
                            {
                                chess = new int[19][19];
                                JOptionPane.showMessageDialog(MyFrame.this, "黑方认输，白方胜利");
                                ms.playMusicwin();
                                try
                                {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e1)
                                {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                ms.playMusicclose();
                                flg = false;

                            } else
                            {
                                chess = new int[19][19];
                                JOptionPane.showMessageDialog(MyFrame.this, "白方认输，黑方胜利");
                                ms.playMusicwin();
                                try
                                {
                                    Thread.sleep(2000);
                                } catch (InterruptedException e1)
                                {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
                                ms.playMusicclose();
                                flg = false;

                            }
                            // 认输玩之后继续选择
                            if (!flg)
                            {
                                Object[] options = { "继续游戏", "退出游戏" };
                                int response = JOptionPane.showOptionDialog(null, "", "继续努力", JOptionPane.YES_OPTION,
                                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                                if (response == 0)
                                {
                                    isRun = false;
                                    isStart = false;
                                    flag = false;
                                    start();
                                }
                                // 选择退出游戏
                                else
                                {
                                    System.exit(0);
                                }
                            }
                        } else
                        {
                            isThread = false;
                        }
                    }
                }
                if(e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 434 && e.getY() <= 469&&stepNum<3)
                {
                    JOptionPane.showMessageDialog(MyFrame.this, "超过三个字之后才可以认输");
                }
                // 游戏规则
                if (e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 271 && e.getY() <= 310)
                {
                    JOptionPane.showMessageDialog(null,
                            "<html><h3> (1)对局双方各执一色棋子空棋盘开局" + "<br/>" + "(2)白黑双方交替落子，且必须在规定时间内完成落子，否则会随机下一个子" + "<br/>"
                                    + "(3)棋子下在棋盘的空白点上，棋子下定后，不得向其它点移动，不得从棋盘上拿掉或拿起另落别处。" + "<br/>"
                                    + "(4)如果哪一方先让五颗子连成线，那么那一方就赢了</h3></html>",
                            "游戏规则", JOptionPane.INFORMATION_MESSAGE);
                }

                // 关于
                if (e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 514 && e.getY() <= 547)
                {
                    JOptionPane.showMessageDialog(null, "@@@@@@这个游戏由我开发@@@@@", "游戏简介", JOptionPane.INFORMATION_MESSAGE);
                }

                // 退出按钮
                if ((e.getX() >= 647 && e.getX() <= 754 && e.getY() >= 591 && e.getY() <= 634))
                {
                    int value = JOptionPane.showConfirmDialog(null, "你确定要退出游戏吗", "退出界面", JOptionPane.YES_NO_OPTION);

                    if (value == JOptionPane.YES_OPTION)
                    {
                        System.exit(0);
                    }
                }

                // 用鼠标选点，但必须在棋盘内。
                int x = e.getX() - 19;
                int y = e.getY() - 108;
                // 判定选中的坐标是否在棋盘里面，要点击了游戏开始，才能取点
                if (x >= 0 && x <= 595 && y >= 0 && y <=684 && (isStart == true))
                {
                    // 计算当前坐标对应数组的下标（x,y）    
                    
                    int xIndex = (x / 32) + (x % 32 >= 16 ? 1 : 0);
                    int yIndex = (y / 32) + (y % 32 >= 16 ? 1 : 0);
                  

                    if (chess[yIndex][xIndex] == 0)
                    {
                        if (flag)
                        {
                            chess[yIndex][xIndex] = 1; // 代表白方在当前位置下了一个子
                            try
                            {
                                Thread.sleep(500);
                            } catch (Exception e1)
                            {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            ms.playMusiclz();
                            repaint();
                            flag = !flag;
                            time = ptime;
                            stepNum = ++stepNum;
                            chessBox[yIndex][xIndex] = stepNum;
                            new Thread(black).start();
                        } else
                        {
                            chess[yIndex][xIndex] = 2; // 代表黑方在当前位置下了一个子
                            try
                            {
                                Thread.sleep(500);
                            } catch (InterruptedException e1)
                            {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            ms.playMusiclz();
                            repaint();
                            flag = !flag;
                            time = ptime;
                            stepNum = ++stepNum;
                            chessBox[yIndex][xIndex] = stepNum;
                            new Thread(white).start();

                        }
                        // 表示游戏已经运行。
                        isRun = true;

                    }
                    // 重画棋子
                    repaint();
                    
                    // 判断输赢的方法。。
                    // winOrDefault( flag, yIndex, xIndex);
                    if (flag)
                    {
                        // fc.iswin()判断是否胜利
                        if (fc.iswin(yIndex, xIndex, chess))
                        {
                            ms.playMusicwin();
                            try
                            {
                                Thread.sleep(2000);
                            } catch (InterruptedException e1)
                            {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            ms.playMusicclose();
                            JOptionPane.showMessageDialog(MyFrame.this, "黑方胜利");

                            // 表示游戏结束。
                            isThread = true;
                            Object[] options = { "继续游戏", "退出游戏" };
                            int response = JOptionPane.showOptionDialog(null, "", "继续努力", JOptionPane.YES_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            if (response == 0)
                            {
                                isRun = false;
                                isStart = false;
                                flag = false;
                                start();
                            }
                            // 选择退出游戏
                            else
                            {
                                System.exit(0);
                            }

                        }
                    } else
                    {
                        if (fc.iswin(yIndex, xIndex, chess))
                        {
                            ms.playMusicwin();
                            try
                            {
                                Thread.sleep(2000);
                            } catch (InterruptedException e1)
                            {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                            ms.playMusicclose();
                            JOptionPane.showMessageDialog(MyFrame.this, "白方胜利");
                            // 线程结束。

                            isThread = true;
                            Object[] options = { "继续游戏", "退出游戏" };
                            int response = JOptionPane.showOptionDialog(null, "", "继续努力", JOptionPane.YES_OPTION,
                                    JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                            if (response == 0)
                            {
                                isRun = false;
                                isStart = false;
                                flag = false;
                                // 调用开始方法。
                                start();
                            }
                            // 选择退出游戏
                            else
                            {
                                System.exit(0);
                            }

                        }
                    }
                }
            }

        });
    }

    // 黑子线程
    Runnable black = new Runnable()
    {

        @Override
        public void run()
        {
            // 接收时间的变量
            int temp = time;
            a: while (true)
            {
                for (int i = 0; i < 10; i++)
                {
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (flag)
                    {
                        isRun = true;
                        break a;
                    }
                }
                time--;
                repaint();
                // 判断线程的状态
                if (isThread)
                {
                    break;
                }
                if (time <= 0)
                {
                    time = temp;
                    while (true)
                    {
                        // 随机下黑子
                        int x = (int) (Math.random() * 19);
                        int y = (int) (Math.random() * 19);

                        if (chess[x][y] == 0)
                        {
                            chess[x][y] = 2;
                            try
                            {
                                Thread.sleep(100);
                            } catch (InterruptedException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            ms.playMusiclz();
                           
                            flag = !flag;
                            stepNum = ++stepNum;
                            chessBox[x][y] = stepNum;
                            break;
                        }
                    }
                    // 表示游戏在运行
                    isRun = true;
                    repaint();
                    new Thread(white).start();
                    break;
                }
            }

        }

    };
    // 白字线程
    Runnable white = new Runnable()
    {

        @Override
        public void run()
        {
            // 接收时间的变量
            int temp = time;

            a: while (true)
            {
                for (int i = 0; i < 10; i++)
                {
                    try
                    {
                        Thread.sleep(100);
                    } catch (InterruptedException e)
                    {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    if (!flag)
                    {
                        isRun = true;
                        break a;
                    }
                }
                time--;
                repaint();
                if (isThread)
                {
                    break;
                }
                if (time <= 0)
                {
                    while (true)
                    {
                        // 随机白子
                        int x = (int) (Math.random() * 19);
                        int y = (int) (Math.random() * 19);
                        if (chess[x][y] == 0)
                        {
                            chess[x][y] = 1;
                            try
                            {
                                Thread.sleep(100);
                            } catch (InterruptedException e)
                            {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            ms.playMusiclz();
                           
                            flag = !flag;
                            stepNum = ++stepNum;
                            chessBox[x][y] = stepNum;
                            break;
                        }
                    }
                    time = temp;
                    repaint();
                    // 表示正在运行
                    isRun = true;
                    new Thread(black).start();
                    break;
                }
            }

        }

    };  

    // 画棋盘
    public void paint(Graphics g)
    {
        BufferedImage img = new BufferedImage(805, 768 + 25, BufferedImage.TYPE_INT_RGB);
        Graphics gb = img.getGraphics();
        try
        {
            gb.drawImage(ImageIO.read(new File("src\\img\\bg1.png")), 0, 25, this);
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        gb.setColor(Color.black);
        // 画格子
        for (int i = 0; i < 19; i++)// 575 576
        {
            gb.drawLine(19, 108 + i * 32, 595, 108 + i * 32);
            gb.drawLine(19 + i * 32, 108, 19 + i * 32, 684);
        }
        // 画点
        for (int i = 0; i < 3; i++)
        {
            for (int j = 0; j < 3; j++)
            {
                gb.fillOval(112 + i * 192, 200 + j * 192, 8, 8);
            }
        }
        // 显示谁在下棋
        gb.setFont(new Font("微软雅黑", Font.BOLD, 30));
        if (flag)
        {
            gb.setColor(Color.WHITE);
            gb.drawString("白方下子：", 90, 740);
            gb.drawString("0:" + (time >= 10 ? time : "0" + time), 355, 745);
        } else
        {
            gb.setColor(Color.black);
            gb.drawString("黑方下子：", 90, 740);
            gb.drawString("0:" + (time >= 10 ? time : "0" + time), 355, 745);
        }

        // 循环遍历白黑棋子
        for (int x = 0; x < 19; x++)
        {
            for (int y = 0; y < 19; y++)
            {
                if (chess[x][y] == 1)
                {
                    gb.setColor(Color.white);
                    gb.fillOval(19 + 32 * y - 10, 108 + 32 * x - 10, 20, 20);
                }
                if (chess[x][y] == 2)
                {
                    gb.setColor(Color.black);
                    gb.fillOval(19 + 32 * y - 10, 108 + 32 * x - 10, 20, 20);
                }

            }

        }
        g.drawImage(img, 0, 0, this);
        repaint();

    }

    // 悔棋的方法
    public void regret()
    {
        if (stepNum >= 5)
        {
            a: for (int i = 0; i < 19; i++)
            {
                for (int j = 0; j < 19; j++)
                {
                    if (chessBox[i][j] == stepNum)// 因为setNum是从0开始的
                    {
                        chess[i][j] = 0;
                        break a;
                    }

                }
            }
            repaint();
            // 步数减一
            stepNum -= 1;
            if ((flag))
            {

                flag = !flag;
                new Thread(black).start();
                // flag为true代表白方落子的状态，如果黑棋要悔棋就必须把flag设为false的状态。
                // flag = !flag;
                repaint();
            } else
            {

                flag = !flag;
                new Thread(white).start();
                // flag为true代表白方落子的状态，如果黑棋要悔棋就必须把flag设为false的状态。
                // flag = !flag;
                repaint();
            }
            time = ptime;

        } else
        {
            JOptionPane.showMessageDialog(null, "必须超过5个子才可以悔棋", "按照规矩来", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    // 调用初始化的方法
    public void start()
    {  
        // 表示游戏已经点击了开始游戏
        isStart = true;
        isThread = false;
        // 重新开始的时候让时间变回原来的时间
        time = ptime;
        new Thread(black).start();
        new Thread(white).start();
        chess = new int[19][19];
        repaint();

    }

    // 判定输赢的方法。
    public void winOrDefault(boolean flag, int yIndex, int xIndex)
    {
        if (flag)
        {
            // fc.iswin()判断是否胜利
            if (fc.iswin(yIndex, xIndex, chess))
            {
                ms.playMusicwin();
                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                ms.playMusicclose();
                JOptionPane.showMessageDialog(MyFrame.this, "黑方胜利");

                // 表示游戏结束。
                isThread = true;
                Object[] options = { "继续游戏", "退出游戏" };
                int response = JOptionPane.showOptionDialog(null, "", "继续努力", JOptionPane.YES_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (response == 0)
                {
                    isRun = false;
                    isStart = false;
                    flag = false;
                    start();
                }
                // 选择退出游戏
                else
                {
                    System.exit(0);
                }

            }
        } else
        {
            if (fc.iswin(yIndex, xIndex, chess))
            {
                ms.playMusicwin();
                try
                {
                    Thread.sleep(2000);
                } catch (InterruptedException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                ms.playMusicclose();
                JOptionPane.showMessageDialog(MyFrame.this, "白方胜利");
                // 线程结束。
                isThread = true;
                Object[] options = { "继续游戏", "退出游戏" };
                int response = JOptionPane.showOptionDialog(null, "", "继续努力", JOptionPane.YES_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
                if (response == 0)
                {
                    isRun = false;
                    isStart = false;
                    flag = false;
                    // 调用开始方法。
                    start();
                }
                // 选择退出游戏
                else
                {
                    System.exit(0);
                }

            }
        }
    }

}
