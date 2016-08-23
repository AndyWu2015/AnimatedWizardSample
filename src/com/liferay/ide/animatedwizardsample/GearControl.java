
package com.liferay.ide.animatedwizardsample;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Path;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Resource;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import com.liferay.ide.animatedwizardsample.GearAnimator.Listener;
import com.liferay.ide.animatedwizardsample.GearAnimator.Page;

/**
 * @author Andy Wu
 */
public class GearControl extends Canvas
{

    private static final int DEFAULT_TIMER_INTERVAL = 10;

    private final Runnable runnable = new Runnable()
    {

        public void run()
        {
            doRun();
        }
    };

    protected synchronized void doRun()
    {
        if( isDisposed() )
        {
            return;
        }

        boolean needsRedraw = advance();

        if( needsRedraw )
        {
            redraw();
        }
        else
        {
            scheduleRun();
        }
    }

    private void scheduleRun()
    {
        getDisplay().timerExec( DEFAULT_TIMER_INTERVAL, runnable );
    }

    private int width;

    private int height;

    private Font baseFont;
    private final List<Resource> resources = new ArrayList<Resource>();

    public static final String RECORDER_PREFERENCE_KEY = "RECORDER_PREFERENCE_KEY";

    public static final int NONE = -1;

    public static final int PAGE_WIDTH = 620;

    public static final int PAGE_HEIGHT = 420;

    public static final int BORDER = 30;

    public static final int GEARS = 7;

    private static final int TEETH = 8;

    private static final float ANGLE = 360 / TEETH;

    private static final double RADIAN = 2 * Math.PI / 360;

    private static final int EXIT = NONE - 1;

    private static final int BACK = EXIT - 1;

    private static final int NEXT = BACK - 1;

    private static final int CHOICES = NEXT - 1;

    private static final String[] TITLES = { "Welcome to Eclipse", "Refresh Resources Automatically?",
        "Show Line Numbers in Editors?", "Check Spelling in Text Editors?", "Execute Jobs in Background?",
        "Encode Text Files with UTF-8?", "Enable Preference Recorder?" };

    static final int BIG_FONT_PX = 48;

    static final int NORMAL_FONT_PX = (int) ( BIG_FONT_PX * .75 );

    private static Color WHITE;

    private static Color GRAY;

    private static Color DARK_GRAY;

    private final List<Listener> listeners = new ArrayList<Listener>();

    private Color purple;

    private Color tooltipColor;

    private Font tooltipFont;

    private Font bigFont;

    private Font hoverFont;

    private Font normalFont;

    private Font numberFont;

    private Image exit;

    private Image exitHover;

    private Image question;

    private final Image[] welcomeImages = new Image[2];

    private final Image[] summaryImages = new Image[2];

    private final Image[] backImages = new Image[2];

    private final Image[] nextImages = new Image[2];

    private final Image[] yesImages = new Image[5];

    private final Image[] noImages = new Image[5];

    private final Page[] pages = new Page[GEARS + 1];

    private final Point[] tooltipPoints = new Point[pages.length];

    private final Path[] gearPaths = new Path[GEARS + 1];

    private final Color[] gearBackground = new Color[2];

    private final Color[] gearForeground = new Color[2];

    private float radius;

    private int pageY;

    private int answerY;

    private int buttonR;

    private long startAnimation;

    private float speed;

    private float angle;

    private boolean overflow;

    private int selection;

    private int oldSelection = NONE;

    private int hover = NONE;

    private int oldHover = NONE;

    private Image pageBuffer;

    private GC pageGC;

    private Image oldPageBuffer;

    private GC oldPageGC;

    private boolean pageBufferUpdated;

    private boolean oldShowOverlay;

    private boolean summaryShown;

    private Rectangle exitBox;

    private Rectangle backBox;

    private Rectangle nextBox;

    public GearControl insance;

    private Display display = getDisplay();

    protected boolean advance()
    {
        boolean needsRedraw = false;

        if( overflow )
        {
            overflow = false;
            needsRedraw = true;
        }

        /*
         * boolean showOverlay = shouldShowOverlay(); if (showOverlay != oldShowOverlay) { oldShowOverlay = showOverlay;
         * updatePage(); needsRedraw = true; }
         */

        if( hover != oldHover )
        {
            needsRedraw = true;
        }

        if( speed >= ANGLE )
        {
            startAnimation = 0;
            return needsRedraw;
        }

        long now = System.currentTimeMillis();
        if( startAnimation == 0 )
        {
            startAnimation = now;
        }

        long timeSinceStart = now - startAnimation;
        speed = timeSinceStart * ANGLE / 1900;
        angle += speed;

        // System.out.println("angle:"+angle);
        // System.out.println("speed:"+speed);

        return true;
    }

    public GearControl( Composite parent, int style )
    {
        super( parent, style );

        WHITE = display.getSystemColor( SWT.COLOR_WHITE );
        GRAY = display.getSystemColor( SWT.COLOR_GRAY );
        DARK_GRAY = display.getSystemColor( SWT.COLOR_DARK_GRAY );

        insance = this;

        insance.addPaintListener( new PaintListener()
        {

            @Override
            public void paintControl( PaintEvent e )
            {
                paint( e.gc );
            }
        } );

        init();

        scheduleRun();
    }

    public void restart()
    {
        angle = 0;
        speed = 0;
    }

    protected final Color createColor( int r, int g, int b )
    {
        Display display = getDisplay();
        Color color = new Color( display, r, g, b );
        resources.add( color );
        return color;
    }

    private void init()
    {
        Font initialFont = getFont();
        FontData[] fontData = initialFont.getFontData();
        for( int i = 0; i < fontData.length; i++ )
        {
            fontData[i].setHeight( 16 );
            fontData[i].setStyle( SWT.BOLD );
        }

        baseFont = new Font( display, fontData );

        bigFont = createFont( BIG_FONT_PX, PAGE_WIDTH, TITLES );
        hoverFont = createFont( BIG_FONT_PX + 6, PAGE_WIDTH, TITLES );
        normalFont = createFont( NORMAL_FONT_PX, PAGE_WIDTH, TITLES );
        numberFont = createFont( 24 );
        tooltipFont = createFont( 16 );

        radius = 32;
        setSize( (int) ( GEARS * 2 * radius ), (int) ( 2 * radius ) );
        pageY = getSize().y + 2 * BORDER;

        // Not selected.
        gearBackground[0] = createColor( 169, 171, 202 );
        gearForeground[0] = createColor( 140, 132, 171 );

        // Selected.
        gearBackground[1] = createColor( 247, 148, 30 );
        gearForeground[1] = createColor( 207, 108, 0 );

        purple = createColor( 43, 34, 84 );
        tooltipColor = createColor( 253, 232, 206 );

        pageBuffer = new Image( display, PAGE_WIDTH, PAGE_HEIGHT );
        pageGC = new GC( pageBuffer );
        pageGC.setAdvanced( true );

        oldPageBuffer = new Image( display, PAGE_WIDTH, PAGE_HEIGHT );
        oldPageGC = new GC( oldPageBuffer );
        oldPageGC.setAdvanced( true );

    }

    private void paint( GC gc )
    {
        int alpha = Math.min( (int) ( 255 * speed / ANGLE ), 255 );

        for( int i = 0; i < GEARS + 1; i++ )
        {
            if( i != selection && ( i < GEARS || summaryShown ) )
            {
                tooltipPoints[i] = paintGear( gc, i, alpha );
            }
        }

        scheduleRun();
    }

    private Point paintGear( GC gc, int i, int alpha )
    {
        double offset = 2 * i * radius;
        double x = BORDER + radius + offset;
        double y = BORDER + radius;
        double r2 = (double) radius * .8f;
        double r3 = (double) radius * .5f;

        int selected = 0;
        double factor = 1;
        if( i == oldSelection )
        {
            if( speed < ANGLE / 2 )
            {
                selected = 1;
            }
        }
        else if( i == selection )
        {
            if( speed >= ANGLE / 2 )
            {
                selected = 1;
                factor += ( ANGLE - speed ) * .02;
            }
            else
            {
                factor += speed * .02;
            }
        }

        boolean hovered = false;
        if( i == hover )
        {
            factor += .1;
            oldHover = hover;
            if( selected == 0 )
            {
                hovered = true;
            }
        }

        double outerR = factor * radius;
        double innerR = factor * r2;
        float angleOffset = ( angle + i * ANGLE ) * ( i % 2 == 1 ? -1 : 1 );

        gc.setForeground( hovered ? DARK_GRAY : gearForeground[selected] );
        gc.setBackground( hovered ? GRAY : gearBackground[selected] );

        Display display = getDisplay();

        // System.out.println(angleOffset);

        Path path = drawGear( gc, display, x, y, outerR, innerR, angleOffset );

        if( gearPaths[i] != null )
        {
            gearPaths[i].dispose();
        }

        gearPaths[i] = path;

        int ovalX = (int) ( x - factor * r3 );
        int ovalY = (int) ( y - factor * r3 );
        int ovalR = (int) ( 2 * factor * r3 );
        gc.setBackground( WHITE );
        gc.fillOval( ovalX, ovalY, ovalR, ovalR );
        gc.drawOval( ovalX, ovalY, ovalR, ovalR );

        if( i == 0 )
        {
            Animator.drawImage( gc, welcomeImages[selected], (int) x, (int) y );
        }
        else if( i < GEARS )
        {
            String number = Integer.toString( i );
            gc.setForeground( selected == 1 ? gearForeground[1] : GRAY );
            gc.setFont( numberFont );
            Animator.drawText( gc, x, y - 1, number );
        }
        else
        {
            Animator.drawImage( gc, summaryImages[selected], (int) x, (int) y );
        }

        return paintBadge( gc, x, y, outerR, i, alpha );
    }

    private Point paintBadge( GC gc, double x, double y, double outerR, int i, int alpha )
    {
        if( selection >= GEARS )
        {
            gc.setAlpha( 255 - alpha );
        }
        else if( oldSelection >= GEARS )
        {
            gc.setAlpha( alpha );
        }

        /*
         * Page page = pages[i]; Answer answer = page.getChoiceAnswer(); if (answer instanceof ImageAnswer) {
         * ImageAnswer imageAnswer = (ImageAnswer)answer; Image image = imageAnswer.getImages()[4]; gc.drawImage(image,
         * (int)(x - image.getBounds().width / 2), (int)(y - outerR - 12)); }
         */

        gc.setAlpha( 255 );
        return new Point( (int) x, (int) ( y + outerR ) );
    }

    private static Path drawGear(
        GC gc, Display display, double cx, double cy, double outerR, double innerR, float angleOffset )
    {
        double radian2 = ANGLE / 2 * RADIAN;
        double radian3 = .06;

        Path path = new Path( display );

        for( int i = 0; i < TEETH; i++ )
        {
            double radian = ( i * ANGLE + angleOffset ) * RADIAN;

            double x = cx + outerR * Math.cos( radian );
            double y = cy - outerR * Math.sin( radian );

            if( i == 0 )
            {
                path.moveTo( (int) x, (int) y );
            }

            double r1 = radian + radian3;
            double r3 = radian + radian2;
            double r2 = r3 - radian3;
            double r4 = r3 + radian2;

            x = cx + innerR * Math.cos( r1 );
            y = cy - innerR * Math.sin( r1 );
            path.lineTo( (int) x, (int) y );

            x = cx + innerR * Math.cos( r2 );
            y = cy - innerR * Math.sin( r2 );
            path.lineTo( (int) x, (int) y );

            x = cx + outerR * Math.cos( r3 );
            y = cy - outerR * Math.sin( r3 );
            path.lineTo( (int) x, (int) y );

            x = cx + outerR * Math.cos( r4 );
            y = cy - outerR * Math.sin( r4 );
            path.lineTo( (int) x, (int) y );
        }

        path.close();
        gc.fillPath( path );
        gc.drawPath( path );
        return path;
    }

    protected final Font createFont( int pixelHeight )
    {
        return createFont( pixelHeight, 0 );
    }

    protected final Font createFont( int pixelHeight, int pixelWidth, String... testStrings )
    {
        if( testStrings.length == 0 )
        {
            pixelWidth = Integer.MAX_VALUE;
            testStrings = new String[] { "Ag" };
        }

        Display display = getDisplay();
        GC fontGC = new GC( display );

        try
        {
            FontData[] fontData = baseFont.getFontData();
            int fontSize = 40;
            while( fontSize > 0 )
            {
                for( int i = 0; i < fontData.length; i++ )
                {
                    fontData[i].setHeight( fontSize );
                    fontData[i].setStyle( SWT.BOLD );
                }

                Font font = new Font( display, fontData );
                fontGC.setFont( font );

                if( isFontSmallEnough( pixelHeight, pixelWidth, fontGC, testStrings ) )
                {
                    resources.add( font );
                    return font;
                }

                font.dispose();
                --fontSize;
            }

            throw new RuntimeException( "Could not create font: " + pixelHeight );
        }
        finally
        {
            fontGC.dispose();
        }
    }

    private boolean isFontSmallEnough( int pixelHeight, int pixelWidth, GC fontGC, String[] testStrings )
    {
        for( String testString : testStrings )
        {
            Point extent = fontGC.stringExtent( testString );
            if( extent.y > pixelHeight || extent.x > pixelWidth )
            {
                return false;
            }
        }

        return true;
    }

    public static void main( String[] args )
    {
        Display display = new Display();

        Shell shell = new Shell( display );

        shell.setText( "Animated Example" );
        shell.setLayout( new FillLayout() );

        final GearControl gear = new GearControl( shell, SWT.NONE );

        // gear.redraw();

        Button button = new Button( shell, SWT.PUSH );
        
        button.setText( "animate" );

        button.addSelectionListener( new SelectionListener()
        {

            @Override
            public void widgetSelected( SelectionEvent e )
            {
                gear.restart();
            }

            @Override
            public void widgetDefaultSelected( SelectionEvent e )
            {

            }
        } );

        shell.open();
        while( !shell.isDisposed() )
        {
            if( !display.readAndDispatch() )
            {
                display.sleep();
            }
        }
        display.dispose();
    }
}
