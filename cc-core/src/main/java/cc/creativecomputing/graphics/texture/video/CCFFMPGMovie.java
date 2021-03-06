package cc.creativecomputing.graphics.texture.video;

import static org.bytedeco.javacpp.avcodec.av_free_packet;
import static org.bytedeco.javacpp.avcodec.avcodec_close;
import static org.bytedeco.javacpp.avcodec.avcodec_decode_video2;
import static org.bytedeco.javacpp.avcodec.avcodec_find_decoder;
import static org.bytedeco.javacpp.avcodec.avcodec_open2;
import static org.bytedeco.javacpp.avcodec.avpicture_fill;
import static org.bytedeco.javacpp.avcodec.avpicture_get_size;
import static org.bytedeco.javacpp.avformat.AVSEEK_FLAG_BACKWARD;
import static org.bytedeco.javacpp.avformat.av_dump_format;
import static org.bytedeco.javacpp.avformat.av_read_frame;
import static org.bytedeco.javacpp.avformat.av_seek_frame;
import static org.bytedeco.javacpp.avformat.avformat_close_input;
import static org.bytedeco.javacpp.avformat.avformat_find_stream_info;
import static org.bytedeco.javacpp.avformat.avformat_open_input;
import static org.bytedeco.javacpp.avutil.AVMEDIA_TYPE_VIDEO;
import static org.bytedeco.javacpp.avutil.AV_NOPTS_VALUE;
import static org.bytedeco.javacpp.avutil.AV_PIX_FMT_RGB24;
import static org.bytedeco.javacpp.avutil.AV_TIME_BASE;
import static org.bytedeco.javacpp.avutil.av_rescale_q;
import static org.bytedeco.javacpp.avutil.av_q2d;
import static org.bytedeco.javacpp.avutil.av_frame_alloc;
import static org.bytedeco.javacpp.avutil.av_frame_get_best_effort_timestamp;
import static org.bytedeco.javacpp.avutil.av_free;
import static org.bytedeco.javacpp.avutil.av_malloc;
import static org.bytedeco.javacpp.avutil.av_image_get_buffer_size;
import static org.bytedeco.javacpp.swscale.SWS_BILINEAR;
import static org.bytedeco.javacpp.swscale.sws_getContext;
import static org.bytedeco.javacpp.swscale.sws_scale;

import java.nio.ByteBuffer;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.avcodec.AVCodec;
import org.bytedeco.javacpp.avcodec.AVCodecContext;
import org.bytedeco.javacpp.avcodec.AVPacket;
import org.bytedeco.javacpp.avcodec.AVPicture;
import org.bytedeco.javacpp.avformat;
import org.bytedeco.javacpp.avformat.AVFormatContext;
import org.bytedeco.javacpp.avformat.AVPacketList;
import org.bytedeco.javacpp.avformat.AVStream;
import org.bytedeco.javacpp.avutil.AVDictionary;
import org.bytedeco.javacpp.avutil.AVFrame;
import org.bytedeco.javacpp.avutil.AVRational;
import org.bytedeco.javacpp.swscale.SwsContext;

import cc.creativecomputing.CCAbstractWindowApp;
import cc.creativecomputing.graphics.texture.CCPixelFormat;
import cc.creativecomputing.graphics.texture.CCPixelInternalFormat;
import cc.creativecomputing.graphics.texture.CCPixelType;
import cc.creativecomputing.io.CCIOException;
import cc.creativecomputing.io.CCIOUtil;

public class CCFFMPGMovie extends CCMovieData{
	
	private AVFormatContext _myFormatContext = new AVFormatContext(null);
	private AVCodecContext _myCodecContext = null;
	private AVCodec _myCodec;
	
	private SwsContext _mySwsContext;
	
	private AVFrame _myFrame;
	private AVFrame _myRGBFrame;

	private int _myVideoStreamIndex = -1;
	private AVStream _myVideoStream;
	private IntPointer _myFrameFinished = new IntPointer();
	private AVPacket _myPacket = new AVPacket();
	
    private BytePointer _myBuffer = null;
	
	private String _myPath;
	private double _myDuration;

	public CCFFMPGMovie(CCAbstractWindowApp theApp, final String thePath) {
		super(theApp);
		_myPath = thePath;
		CCFFMPEG.init();
		
		// Open video file
        if (avformat_open_input(_myFormatContext, _myPath, null, null) != 0) {
            throw new CCIOException("Could not open file");
        }
        
        // Retrieve stream information
        if (avformat_find_stream_info(_myFormatContext, (PointerPointer<?>)null) < 0) {
            throw new CCIOException("Couldn't find stream information");
        }
        
        _myVideoStreamIndex = _myFormatContext.video_codec_id();
        _myVideoStream = _myFormatContext.streams(_myVideoStreamIndex);
        _myCodecContext = _myVideoStream.codec();
        
        if(_myCodecContext == null){
        	throw new CCIOException("Didn't find a video stream");
        }
        
        // Find the decoder for the video stream
        _myCodec = avcodec_find_decoder(_myCodecContext.codec_id());
        if (_myCodec == null) {
        	throw new CCIOException("Unsupported codec!");
        }
        // Open codec
        if (avcodec_open2(_myCodecContext, _myCodec, (AVDictionary)null) < 0) {
        	throw new CCIOException("Could not open codec");
        }
        
        // Allocate video frame
        _myFrame = av_frame_alloc();

        // Allocate an AVFrame structure
        _myRGBFrame = av_frame_alloc();
        
        // Determine required buffer size and allocate buffer
        int myNumberOfBytes = avpicture_get_size(AV_PIX_FMT_RGB24, _myCodecContext.width(), _myCodecContext.height());
        _myBuffer = new BytePointer(av_malloc(myNumberOfBytes));
        
        // Assign appropriate parts of buffer to image planes in pFrameRGB
        // Note that pFrameRGB is an AVFrame, but AVFrame is a superset
        // of AVPicture
        avpicture_fill(
        	new AVPicture(_myRGBFrame), 
        	_myBuffer, 
        	AV_PIX_FMT_RGB24,
        	_myCodecContext.width(), 
        	_myCodecContext.height()
        );
        
        // initialize SWS context for software scaling
        _mySwsContext = sws_getContext(
        	_myCodecContext.width(), 
        	_myCodecContext.height(),
        	_myCodecContext.pix_fmt(), 
        	_myCodecContext.width(), 
        	_myCodecContext.height(),
        	AV_PIX_FMT_RGB24, 
        	SWS_BILINEAR, 
        	null, 
        	null, 
        	(DoublePointer)null
        );
        
        _myWidth = _myCodecContext.width();
    	_myHeight = _myCodecContext.height();
    	_myPixelInternalFormat = CCPixelInternalFormat.RGB;
    	
    	_myDuration = _myFormatContext.duration() / (double)AV_TIME_BASE;
		pixelFormat(CCPixelFormat.RGB);
		_myPixelType = CCPixelType.UNSIGNED_BYTE;

		_myIsDataCompressed = false;
		_myMustFlipVertically = true;
	}
	
	public void printInfo(){
		// Dump information about file onto standard error
        av_dump_format(_myFormatContext, 0, _myPath, 0);
	}
	
	public static void main(String[] args) {
		CCFFMPGMovie myMovie = new CCFFMPGMovie(null, CCIOUtil.dataPath("demo/videos/kaki.mov"));
		myMovie.printInfo();
	}

	@Override
	public void rate(float theSpeed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public float rate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float frameRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void goToBeginning() {
		time(0);
	}

	@Override
	public float duration() {
		return (float)_myDuration;
	}
	
	@Override
	public float time(){
		return (float)_myTime;
	}
	
	
	  
	@Override
	public void time(float theTime){
		int seek_pos = (int)(theTime * AV_TIME_BASE);
		int seek_flags = theTime < _myPTS ? AVSEEK_FLAG_BACKWARD : 0;
		
		AVRational myAVTimeBaseQ = new AVRational();
        myAVTimeBaseQ.num(1);
        myAVTimeBaseQ.den(AV_TIME_BASE);
        long seek_target = av_rescale_q(seek_pos,myAVTimeBaseQ, _myFormatContext.streams(_myVideoStreamIndex).time_base());
        av_seek_frame(_myFormatContext, _myVideoStreamIndex, seek_target, seek_flags);
        
		_myVideoClock = _myPTS = _myTime = theTime;
	}
	
	int[] _myFrameFinishedA = new int[1];
	
	private boolean _myIsFirstFrame = true;
	
	private double _myPTS;
	private double _myVideoClock; // pts of last decoded frame / predicted pts of next decoded frame
	private double _myTime = 0;

	private double synchronizeVideo(AVFrame theFrame, double thePTS) {

		double frame_delay;

		if (thePTS != 0) {
			/* if we have pts, set video clock to it */
			_myVideoClock = thePTS;
		} else {
			/* if we aren't given a pts, set it to the clock */
			thePTS = _myVideoClock;
		}
		/* update the video clock */
		frame_delay = av_q2d(_myVideoStream.codec().time_base());
		/* if we are repeating a frame, adjust clock accordingly */
		frame_delay += theFrame.repeat_pict() * (frame_delay * 0.5);
		_myVideoClock += frame_delay;
		return thePTS;
	}
	
	private double timestampToTime(double theTimeStamp){
		return theTimeStamp * av_q2d(_myVideoStream.time_base());
	}

	@Override
	public void update(float theDeltaTime) {
		if(_myIsRunning)_myTime += theDeltaTime;
        
        

        
        if(_myPTS > _myTime){
        	return;
        }
        
        if (av_read_frame(_myFormatContext, _myPacket) < 0) {
        	_myMovieEvents.proxy().onEnd();
        	if(_myDoRepeat){
        		play(true);
        	}
        	return;
        }
        	
        // Is this a packet from the video stream?
        if (_myPacket.stream_index() == _myVideoStreamIndex) {
        	// Decode video frame
        	int leng1 = avcodec_decode_video2(_myCodecContext, _myFrame, _myFrameFinishedA, _myPacket);
        	
        	if(_myPacket.dts() != AV_NOPTS_VALUE) {
        		_myPTS = timestampToTime(av_frame_get_best_effort_timestamp(_myFrame));
        	} else {
        		_myPTS = 0;
        	}

        	// Did we get a video frame?
        	if (_myFrameFinishedA[0] != 0) {
        		_myPTS = synchronizeVideo(_myFrame, _myPTS);
        		
        		// Convert the image from its native format to RGB
        		sws_scale(
        			_mySwsContext, 
                    _myFrame.data(), 
                    _myFrame.linesize(), 0,
                    _myCodecContext.height(), 
                    _myRGBFrame.data(), 
                    _myRGBFrame.linesize()
        		);
        		byte[] bytes = new byte[_myWidth * 3];
        		ByteBuffer myBuffer = ByteBuffer.allocate(_myWidth * 3 * _myHeight);
        		int l = _myRGBFrame.linesize(0);
        		for(int y = 0; y < _myHeight; y++) {
        			_myRGBFrame.data(0).position(y * l).get(bytes);
        			myBuffer.put(bytes);
//                        stream.write(bytes);
        		}
        		myBuffer.rewind();
        		buffer(myBuffer);
        		
        		
        		if (_myIsFirstFrame) {
        			_myIsFirstFrame = false;
        			_myListener.proxy().onInit(this);
        		} else {
        			_myListener.proxy().onUpdate(this);
        		}
        	}
        }

        // Free the packet that was allocated by av_read_frame
        av_free_packet(_myPacket);
        
	}

	@Override
	public void post() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void finalize() throws Throwable {
		// Free the RGB image
        av_free(_myBuffer);
        av_free(_myRGBFrame);

        // Free the YUV frame
        av_free(_myFrame);

        // Close the codec
        avcodec_close(_myCodecContext);

        // Close the video file
        avformat_close_input(_myFormatContext);
	}
}
