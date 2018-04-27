# jave
Distribution of the sauronsoftware (http://www.sauronsoftware.it/projects/jave/manual.php)

# JAVE manual

* [Installation and requirements](#1)
* [Audio/video encoding](#2)
* [Encoding attributes](#3)
    * [Audio encoding attributes](#3.1)
    * [Video encoding attributes](#3.2)
* [Monitoring the transcoding operation](#4)
* [Transcoding failures](#5)
* [Getting informations about a multimedia file](#6)
* [Using an alternative ffmpeg executable](#7)
* [Supported container formats](#8)
* [Built-in decoders and encoders](#9)
* [Examples](#10)

<a name="1"></a>

# Installation and requirements

In order to use JAVE in your Java application, you have to add the file _jave-1.0.jar_ in your application CLASSPATH.

JAVE runs on a Java Runtime Environment J2SE v.1.4 or later.

JAVE includes and uses a _ffmpeg_ executable built for Windows and Linux operating systems on i386/32 bit hardware platforms. In order to run JAVE on other platforms you have to replace the built-in ffmpeg executable with another one suitable for your needs. This is very simple, once you have built your own ffmpeg binaries. The operation is described in the "[Using an alternative ffmpeg executable](#7)" section.

<a name="2"></a>

# Audio/video encoding

The most important JAVE class is _it.sauronsoftware.jave.Encoder_. _Encoder_ objects expose many methods for multimedia transcoding. In order to use JAVE, you always have to create an _Encoder_ istance:

    Encoder encoder = new Encoder();

Once the instance has been created, you can start transcoding calling the _encode()_ method:

```java
public void encode(java.io.File source,
                   java.io.File target,
                   it.sauronsoftware.jave.EncodingAttributes attributes)
            throws java.lang.IllegalArgumentException,
                   it.sauronsoftware.jave.InputFormatException,
                   it.sauronsoftware.jave.EncoderException
```

The first parameter, _source_, represents the source file to decode.

The second parameter, _target_, is the target file that will be created and encoded.

The _attributes_ parameter, whose type is _it.sauronsoftware.jave.EncodingAttributes_, is a data structure containing any information needed by the encoder.

Please note that a call to _encode()_ is a blocking one: the method will return only once the transcoding operation has been completed (or failed). If you are interested in monitoring the transcoding operation take a look to the "[Monitoring the transcoding operation](#4)" section.

<a name="3"></a>

# Encoding attributes

To specify your preferences about the transcoding operation you have to supply an _it.sauronsoftware.jave.EncodingAttributes_ instance to the _encode()_ call. You can create your own _EncodingAttributes_ instance, and you can populate it with the following methods:

*   `public void setAudioAttributes(it.sauronsoftware.jave.AudioAttributes audioAttributes)`  
    It sets the audio encoding attributes. If never called on a new _EncodingAttributes_ instance, or if the given parameter is _null_, no audio stream will be included in the encoded file. See also "[Audio encoding attributes](#3.1)".
*   `public void setVideoAttributes(it.sauronsoftware.jave.AudioAttributes videoAttributes)`  
    It sets the video encoding attributes. If never called on a new _EncodingAttributes_ instance, or if the given parameter is _null_, no video stream will be included in the encoded file. See also "[Video encoding attributes](#3.2)".
*   `public void setFormat(java.lang.String format)`  
    It sets the format of the streams container that will be used for the new encoded file. The given parameter represents the format name. An encoding format name is valid and supported only if it appears in the list returned by the _getSupportedEncodingFormats()_ method of the _Encoder_ instance in use.
*   `public void setOffset(java.lang.Float offset)`  
    It sets an offset for the transcoding operation. The source file will be re-encoded starting at _offset_ seconds since its beginning. In example if you'd like to cut the first five seconds of the source file, you should call _setOffset(5)_ on the _EncodingAttributes_ object passed to the encoder.
*   `public void setDuration(java.lang.Float duration)`  
    It sets a duration for the transcoding operation. Only _duration_ seconds of the source will be re-encoded in the target file. In example if you'd like to extract and transcode a portion of thirty seconds from the source, you should call _setDuration(30)_ on the _EncodingAttributes_ object passed to the encoder.

<a name="3.1"></a>

## Audio encoding attributes

Audio encoding attributes are represented by the instances of the _it.sauronsoftware.jave.AudioAttributes_ class. The available methods on this kind of objects are:

*   `public void setCodec(java.lang.String codec)`  
    It sets the name of the codec that will be used for the transcoding of the audio stream. You have to choose a value from the list returned by the _getAudioEncoders()_ method of the current _Encoder_ instance. Otherwise you can pass the _AudioAttributes.DIRECT_STREAM_COPY_ special value, that requires the copy of the original audio stream from the source file.
*   `public void setBitRate(java.lang.Integer bitRate)`  
    It sets the bitrate value for the new re-encoded audio stream. If no bitrate value is set, a default one will be picked by the encoder. The value should be expressed in bits per second. In example if you want a 128 kb/s bitrate you should call _setBitRate(new Integer(128000))_.
*   `public void setSamplingRate(java.lang.Integer bitRate)`  
    It sets the sampling rate for the new re-encoded audio stream. If no sampling-rate value is set, a default one will be picked by the encoder. The value should be expressed in hertz. In example if you want a CD-like 44100 Hz sampling-rate, you should call _setSamplingRate(new Integer(44100))_.
*   `public void setChannels(java.lang.Integer channels)`  
    It sets the number of the audio channels that will be used in the re-encoded audio stream (1 = mono, 2 = stereo). If no channels value is set, a default one will be picked by the encoder.
*   `public void setVolume(java.lang.Integer volume)`  
    This method can be called to alter the volume of the audio stream. A value of 256 means no volume change. So a value less than 256 is a volume decrease, while a value greater than 256 will increase the volume of the audio stream.

<a name="3.2"></a>

## Video encoding attributes

Video encoding attributes are represented by the instances of the _it.sauronsoftware.jave.VideoAttributes_ class. The available methods on this kind of objects are:

*   `public void setCodec(java.lang.String codec)`  
    It sets the name of the codec that will be used for the transcoding of the video stream. You have to choose a value from the list returned by the _getVideoEncoders()_ method of the current _Encoder_ instance. Otherwise you can pass the _VideoAttributes.DIRECT_STREAM_COPY_ special value, that requires the copy of the original video stream from the source file.
*   `public void setTag(java.lang.String tag)`  
    It sets the tag/fourcc value associated to the re-encoded video stream. If no value is set a default one will be picked by the encoder. The tag value is often used by multimedia players to choose which video decoder run on the stream. In example a MPEG 4 video stream with a "DIVX" tag value will be decoded with the default DivX decoder used by the player. And, by the way, this is exactly what a DivX is: a MPEG 4 video stream with an attached "DIVX" tag/fourcc value!
*   `public void setBitRate(java.lang.Integer bitRate)`  
    It sets the bitrate value for the new re-encoded video stream. If no bitrate value is set, a default one will be picked by the encoder. The value should be expressed in bits per second. In example if you want a 360 kb/s bitrate you should call _setBitRate(new Integer(360000))_.
*   `public void setFrameRate(java.lang.Integer bitRate)`  
    It sets the frame rate value for the new re-encoded audio stream. If no bitrate frame-rate is set, a default one will be picked by the encoder. The value should be expressed in frames per second. In example if you want a 30 f/s frame-rate you should call _setFrameRate(new Integer(30))_.
*   `public void setSize(it.sauronsoftware.jave.VideoSize size)`  
    It sets the size and the proportion of the images in the video stream. If no value is set, the encoder will preserve the original size and proportion. Otherwise you can pass a _it.sauronsoftware.java.VideoSize_ instance, with your preferred size. You can set the width and the height of the new encoded video, with pixel values, scaling the original one. In example if you want to scale the video to 512 px in width and 384px in height you should call _setSize(new VideoSize(512, 384))_.

<a name="4"></a>

# Monitoring the transcoding operation

You can monitor a transcoding operation with a listener. JAVE defines the _it.sauronsoftware.jave.EncoderProgressListener_ interface. This interface could be implemented by your application, and concrete _EncoderProgressListener_ instances can be passed to the encoder. The encoder will call your listener methods every time a significant event occurs. To pass an _EncoderProgressListener_ to the encoder you should use this definition of the _encode()_ method:

```java
public void encode(java.io.File source,
                   java.io.File target,
                   it.sauronsoftware.jave.EncodingAttributes attributes,
                   it.sauronsoftware.jave.EncoderProgressListener listener)
            throws java.lang.IllegalArgumentException,
                   it.sauronsoftware.jave.InputFormatException,
                   it.sauronsoftware.jave.EncoderException
```

To implemen the _EncoderProgressListener_ interface you have to define all of the following methods:

*   `public void sourceInfo(it.sauronsoftware.jave.MultimediaInfo info)`  
    The encoder calls this method after the source file has been analized. The _info_ parameter is an instance of the _it.sauronsoftware.jave.MultimediaInfo_ class and it represents informations about the source audio and video streams and their container.
*   `public void progress(int permil)`  
    This method is called by the encoder every time a progress in the encoding operation has been done. The _permil_ parameter is a value representing the point reached by the current operation and its range is from 0 (operation just started) to 1000 (operation completed).
*   `public void message(java.lang.String message)`  
    This method is called by the encoder to notify a message regarding the transcoding operation (usually the message is a warning).

<a name="5"></a>

# Transcoding failures

Of course, a transcoding operation could fail. Then the _encode()_ method will propagate an exception. Depending on what is happened, the exception will be one of the following:

*   _java.lang.IllegalArgumentException_  
    The transcoding operation has never started since the encoding attributes passed to the encoder has been recognized as invalid. Usualy this occurs when the EncodingAttributes instance given to the encoder asks the encoding of a container with no audio and no video streams (both _AudioAttributes_ and _VideoAttribues_ attributes are _null_ or not set).
*   _it.sauronsoftware.jave.InputFormatException_  
    The source file can't be decoded. It occurs when the source file container, the video stream format or the audio stream format are not supported by the decoder. You can check for supported containers and plugged decoders calling the encoder methods _getSupportedDecodingFormats()_, _getAudioDecoders()_ and _getVideoDecoders()_.
*   _it.sauronsoftware.jave.EncoderExpection_  
    The operation has failed during the trancoding due to an internal error. You should check the exception message, and you can also use an [_EncoderProgressListener_](#4) instance to check any message issued by the encoder.

<a name="6"></a>

# Getting informations about a multimedia file

You can get informations about an existing multimedia file before transcoding it, calling the encoder _getInfo()_ method. The _getInfo()_ method gives you informations about the container used by the file and about its wrapped audio and video streams:

```java
public it.sauronsoftware.jave.MultimediaInfo getInfo(java.io.File source)
                                             throws it.sauronsoftware.jave.InputFormatException,
                                                    it.sauronsoftware.jave.EncoderException
```

An _it.sauronsoftware.jave.MultimediaInfo_ object encapsulates information on the whole multimedia content and its streams, using instances of _it.sauronsoftware.jave.AudioInfo_ and _it.sauronsoftware.jave.VideoInfo_ to describe the wrapped audio and video. These objects are similar to the _[EncodingAttributes](#3)_, _[AudioAttributes](#3.1)_ and _[VideoAttributes](#3.2)_ ones, but they works in a read-only mode. Check the JAVE API javadoc documentation, bundled with the JAVE distribution, to gain more details about them.

<a name="7"></a>

# Using an alternative ffmpeg executable

JAVE is not pure Java: it acts as a wrapper around an _ffmpeg_ ([http://ffmpeg.mplayerhq.hu/](http://ffmpeg.mplayerhq.hu/)) executable. ffmpeg is an open source and free software project entirely written in C, so its executables cannot be easily ported from a machine to another. You need a pre-compiled version of ffmpeg in order to run JAVE on your target machine. The JAVE distribution includes two pre-compiled executables of ffmpeg: a Windows one and a Linux one, both compiled for i386/32 bit hardware achitectures. This should be enough in most cases. If it is not enough for your specific situation, you can still run JAVE, but you need to obtain a platform specific ffmpeg executable. Check the Internet for it. You can even build it by yourself getting the code (and the documentation to build it) on the official ffmpeg site. Once you have obtained a ffmpeg executable suitable for your needs, you have to hook it in the JAVE library. That's a plain operation. JAVE gives you an abstract class called _it.sauronsoftware.jave.FFMPEGLocator_. Extend it. All you have to do is to define the following method:

```java
public java.lang.String getFFMPEGExecutablePath()
```

This method should return a file system based path to your custom ffmpeg executable.

Once your class is ready, suppose you have called it _MyFFMPEGExecutableLocator_, you have to create an alternate encoder that uses it instead of the default locator:

```java
Encoder encoder = new Encoder(new MyFFMPEGExecutableLocator())
```

You can use the same procedure also to switch to other versions of ffmpeg, even if you are on a platform covered by the executables bundled in the JAVE distribution.

Anyway be careful and test ever your application: JAVE it's not guaranteed to work properly with custom ffmpeg executables different from the bundled ones.

<a name="8"></a>

# Supported container formats

The JAVE built-in ffmpeg executable gives support for the following multimedia container formats:

## Decoding

<table>

<tbody>

<tr>

<th>Formato</th>

<th>Descrizione</th>

</tr>

<tr>

<td>4xm</td>

<td>4X Technologies format</td>

</tr>

<tr>

<td>MTV</td>

<td>MTV format</td>

</tr>

<tr>

<td>RoQ</td>

<td>Id RoQ format</td>

</tr>

<tr>

<td>aac</td>

<td>ADTS AAC</td>

</tr>

<tr>

<td>ac3</td>

<td>raw ac3</td>

</tr>

<tr>

<td>aiff</td>

<td>Audio IFF</td>

</tr>

<tr>

<td>alaw</td>

<td>pcm A law format</td>

</tr>

<tr>

<td>amr</td>

<td>3gpp amr file format</td>

</tr>

<tr>

<td>apc</td>

<td>CRYO APC format</td>

</tr>

<tr>

<td>ape</td>

<td>Monkey's Audio</td>

</tr>

<tr>

<td>asf</td>

<td>asf format</td>

</tr>

<tr>

<td>au</td>

<td>SUN AU Format</td>

</tr>

<tr>

<td>avi</td>

<td>avi format</td>

</tr>

<tr>

<td>avs</td>

<td>AVISynth</td>

</tr>

<tr>

<td>bethsoftvid</td>

<td>Bethesda Softworks 'Daggerfall' VID format</td>

</tr>

<tr>

<td>c93</td>

<td>Interplay C93</td>

</tr>

<tr>

<td>daud</td>

<td>D-Cinema audio format</td>

</tr>

<tr>

<td>dsicin</td>

<td>Delphine Software International CIN format</td>

</tr>

<tr>

<td>dts</td>

<td>raw dts</td>

</tr>

<tr>

<td>dv</td>

<td>DV video format</td>

</tr>

<tr>

<td>dxa</td>

<td>dxa</td>

</tr>

<tr>

<td>ea</td>

<td>Electronic Arts Multimedia Format</td>

</tr>

<tr>

<td>ea_cdata</td>

<td>Electronic Arts cdata</td>

</tr>

<tr>

<td>ffm</td>

<td>ffm format</td>

</tr>

<tr>

<td>film_cpk</td>

<td>Sega FILM/CPK format</td>

</tr>

<tr>

<td>flac</td>

<td>raw flac</td>

</tr>

<tr>

<td>flic</td>

<td>FLI/FLC/FLX animation format</td>

</tr>

<tr>

<td>flv</td>

<td>flv format</td>

</tr>

<tr>

<td>gif</td>

<td>GIF Animation</td>

</tr>

<tr>

<td>gxf</td>

<td>GXF format</td>

</tr>

<tr>

<td>h261</td>

<td>raw h261</td>

</tr>

<tr>

<td>h263</td>

<td>raw h263</td>

</tr>

<tr>

<td>h264</td>

<td>raw H264 video format</td>

</tr>

<tr>

<td>idcin</td>

<td>Id CIN format</td>

</tr>

<tr>

<td>image2</td>

<td>image2 sequence</td>

</tr>

<tr>

<td>image2pipe</td>

<td>piped image2 sequence</td>

</tr>

<tr>

<td>ingenient</td>

<td>Ingenient MJPEG</td>

</tr>

<tr>

<td>ipmovie</td>

<td>Interplay MVE format</td>

</tr>

<tr>

<td>libnut</td>

<td>nut format</td>

</tr>

<tr>

<td>m4v</td>

<td>raw MPEG4 video format</td>

</tr>

<tr>

<td>matroska</td>

<td>Matroska File Format</td>

</tr>

<tr>

<td>mjpeg</td>

<td>MJPEG video</td>

</tr>

<tr>

<td>mm</td>

<td>American Laser Games MM format</td>

</tr>

<tr>

<td>mmf</td>

<td>mmf format</td>

</tr>

<tr>

<td>mov,mp4,m4a,3gp,3g2,mj2</td>

<td>QuickTime/MPEG4/Motion JPEG 2000 format</td>

</tr>

<tr>

<td>mp3</td>

<td>MPEG audio layer 3</td>

</tr>

<tr>

<td>mpc</td>

<td>musepack</td>

</tr>

<tr>

<td>mpc8</td>

<td>musepack8</td>

</tr>

<tr>

<td>mpeg</td>

<td>MPEG1 System format</td>

</tr>

<tr>

<td>mpegts</td>

<td>MPEG2 transport stream format</td>

</tr>

<tr>

<td>mpegtsraw</td>

<td>MPEG2 raw transport stream format</td>

</tr>

<tr>

<td>mpegvideo</td>

<td>MPEG video</td>

</tr>

<tr>

<td>mulaw</td>

<td>pcm mu law format</td>

</tr>

<tr>

<td>mxf</td>

<td>MXF format</td>

</tr>

<tr>

<td>nsv</td>

<td>NullSoft Video format</td>

</tr>

<tr>

<td>nut</td>

<td>nut format</td>

</tr>

<tr>

<td>nuv</td>

<td>NuppelVideo format</td>

</tr>

<tr>

<td>ogg</td>

<td>Ogg format</td>

</tr>

<tr>

<td>psxstr</td>

<td>Sony Playstation STR format</td>

</tr>

<tr>

<td>rawvideo</td>

<td>raw video format</td>

</tr>

<tr>

<td>redir</td>

<td>Redirector format</td>

</tr>

<tr>

<td>rm</td>

<td>rm format</td>

</tr>

<tr>

<td>rtsp</td>

<td>RTSP input format</td>

</tr>

<tr>

<td>s16be</td>

<td>pcm signed 16 bit big endian format</td>

</tr>

<tr>

<td>s16le</td>

<td>pcm signed 16 bit little endian format</td>

</tr>

<tr>

<td>s8</td>

<td>pcm signed 8 bit format</td>

</tr>

<tr>

<td>sdp</td>

<td>SDP</td>

</tr>

<tr>

<td>shn</td>

<td>raw shorten</td>

</tr>

<tr>

<td>siff</td>

<td>Beam Software SIFF</td>

</tr>

<tr>

<td>smk</td>

<td>Smacker Video</td>

</tr>

<tr>

<td>sol</td>

<td>Sierra SOL Format</td>

</tr>

<tr>

<td>swf</td>

<td>Flash format</td>

</tr>

<tr>

<td>thp</td>

<td>THP</td>

</tr>

<tr>

<td>tiertexseq</td>

<td>Tiertex Limited SEQ format</td>

</tr>

<tr>

<td>tta</td>

<td>true-audio</td>

</tr>

<tr>

<td>txd</td>

<td>txd format</td>

</tr>

<tr>

<td>u16be</td>

<td>pcm unsigned 16 bit big endian format</td>

</tr>

<tr>

<td>u16le</td>

<td>pcm unsigned 16 bit little endian format</td>

</tr>

<tr>

<td>u8</td>

<td>pcm unsigned 8 bit format</td>

</tr>

<tr>

<td>vc1</td>

<td>raw vc1</td>

</tr>

<tr>

<td>vmd</td>

<td>Sierra VMD format</td>

</tr>

<tr>

<td>voc</td>

<td>Creative Voice File format</td>

</tr>

<tr>

<td>wav</td>

<td>wav format</td>

</tr>

<tr>

<td>wc3movie</td>

<td>Wing Commander III movie format</td>

</tr>

<tr>

<td>wsaud</td>

<td>Westwood Studios audio format</td>

</tr>

<tr>

<td>wsvqa</td>

<td>Westwood Studios VQA format</td>

</tr>

<tr>

<td>wv</td>

<td>WavPack</td>

</tr>

<tr>

<td>yuv4mpegpipe</td>

<td>YUV4MPEG pipe format</td>

</tr>

</tbody>

</table>

## Encoding

<table>

<tbody>

<tr>

<th>Formato</th>

<th>Descrizione</th>

</tr>

<tr>

<td>3g2</td>

<td>3gp2 format</td>

</tr>

<tr>

<td>3gp</td>

<td>3gp format</td>

</tr>

<tr>

<td>RoQ</td>

<td>Id RoQ format</td>

</tr>

<tr>

<td>ac3</td>

<td>raw ac3</td>

</tr>

<tr>

<td>adts</td>

<td>ADTS AAC</td>

</tr>

<tr>

<td>aiff</td>

<td>Audio IFF</td>

</tr>

<tr>

<td>alaw</td>

<td>pcm A law format</td>

</tr>

<tr>

<td>amr</td>

<td>3gpp amr file format</td>

</tr>

<tr>

<td>asf</td>

<td>asf format</td>

</tr>

<tr>

<td>asf_stream</td>

<td>asf format</td>

</tr>

<tr>

<td>au</td>

<td>SUN AU Format</td>

</tr>

<tr>

<td>avi</td>

<td>avi format</td>

</tr>

<tr>

<td>crc</td>

<td>crc testing format</td>

</tr>

<tr>

<td>dv</td>

<td>DV video format</td>

</tr>

<tr>

<td>dvd</td>

<td>MPEG2 PS format (DVD VOB)</td>

</tr>

<tr>

<td>ffm</td>

<td>ffm format</td>

</tr>

<tr>

<td>flac</td>

<td>raw flac</td>

</tr>

<tr>

<td>flv</td>

<td>flv format</td>

</tr>

<tr>

<td>framecrc</td>

<td>framecrc testing format</td>

</tr>

<tr>

<td>gif</td>

<td>GIF Animation</td>

</tr>

<tr>

<td>gxf</td>

<td>GXF format</td>

</tr>

<tr>

<td>h261</td>

<td>raw h261</td>

</tr>

<tr>

<td>h263</td>

<td>raw h263</td>

</tr>

<tr>

<td>h264</td>

<td>raw H264 video format</td>

</tr>

<tr>

<td>image2</td>

<td>image2 sequence</td>

</tr>

<tr>

<td>image2pipe</td>

<td>piped image2 sequence</td>

</tr>

<tr>

<td>libnut</td>

<td>nut format</td>

</tr>

<tr>

<td>m4v</td>

<td>raw MPEG4 video format</td>

</tr>

<tr>

<td>matroska</td>

<td>Matroska File Format</td>

</tr>

<tr>

<td>mjpeg</td>

<td>MJPEG video</td>

</tr>

<tr>

<td>mmf</td>

<td>mmf format</td>

</tr>

<tr>

<td>mov</td>

<td>mov format</td>

</tr>

<tr>

<td>mp2</td>

<td>MPEG audio layer 2</td>

</tr>

<tr>

<td>mp3</td>

<td>MPEG audio layer 3</td>

</tr>

<tr>

<td>mp4</td>

<td>mp4 format</td>

</tr>

<tr>

<td>mpeg</td>

<td>MPEG1 System format</td>

</tr>

<tr>

<td>mpeg1video</td>

<td>MPEG video</td>

</tr>

<tr>

<td>mpeg2video</td>

<td>MPEG2 video</td>

</tr>

<tr>

<td>mpegts</td>

<td>MPEG2 transport stream format</td>

</tr>

<tr>

<td>mpjpeg</td>

<td>Mime multipart JPEG format</td>

</tr>

<tr>

<td>mulaw</td>

<td>pcm mu law format</td>

</tr>

<tr>

<td>null</td>

<td>null video format</td>

</tr>

<tr>

<td>nut</td>

<td>nut format</td>

</tr>

<tr>

<td>ogg</td>

<td>Ogg format</td>

</tr>

<tr>

<td>psp</td>

<td>psp mp4 format</td>

</tr>

<tr>

<td>rawvideo</td>

<td>raw video format</td>

</tr>

<tr>

<td>rm</td>

<td>rm format</td>

</tr>

<tr>

<td>rtp</td>

<td>RTP output format</td>

</tr>

<tr>

<td>s16be</td>

<td>pcm signed 16 bit big endian format</td>

</tr>

<tr>

<td>s16le</td>

<td>pcm signed 16 bit little endian format</td>

</tr>

<tr>

<td>s8</td>

<td>pcm signed 8 bit format</td>

</tr>

<tr>

<td>svcd</td>

<td>MPEG2 PS format (VOB)</td>

</tr>

<tr>

<td>swf</td>

<td>Flash format</td>

</tr>

<tr>

<td>u16be</td>

<td>pcm unsigned 16 bit big endian format</td>

</tr>

<tr>

<td>u16le</td>

<td>pcm unsigned 16 bit little endian format</td>

</tr>

<tr>

<td>u8</td>

<td>pcm unsigned 8 bit format</td>

</tr>

<tr>

<td>vcd</td>

<td>MPEG1 System format (VCD)</td>

</tr>

<tr>

<td>vob</td>

<td>MPEG2 PS format (VOB)</td>

</tr>

<tr>

<td>voc</td>

<td>Creative Voice File format</td>

</tr>

<tr>

<td>wav</td>

<td>wav format</td>

</tr>

<tr>

<td>yuv4mpegpipe</td>

<td>YUV4MPEG pipe format</td>

</tr>

</tbody>

</table>

<a name="9"></a>

# Built-in decoders and encoders

The JAVE built-in ffmpeg executable contains the following decoders and encoders:

## Audio decoders

<table>

<tbody>

<tr>

<td>adpcm_4xm</td>

<td>adpcm_adx</td>

<td>adpcm_ct</td>

<td>adpcm_ea</td>

<td>adpcm_ea_r1</td>

</tr>

<tr>

<td>adpcm_ea_r2</td>

<td>adpcm_ea_r3</td>

<td>adpcm_ea_xas</td>

<td>adpcm_ima_amv</td>

<td>adpcm_ima_dk3</td>

</tr>

<tr>

<td>adpcm_ima_dk4</td>

<td>adpcm_ima_ea_eacs</td>

<td>adpcm_ima_ea_sead</td>

<td>adpcm_ima_qt</td>

<td>adpcm_ima_smjpeg</td>

</tr>

<tr>

<td>adpcm_ima_wav</td>

<td>adpcm_ima_ws</td>

<td>adpcm_ms</td>

<td>adpcm_sbpro_2</td>

<td>adpcm_sbpro_3</td>

</tr>

<tr>

<td>adpcm_sbpro_4</td>

<td>adpcm_swf</td>

<td>adpcm_thp</td>

<td>adpcm_xa</td>

<td>adpcm_yamaha</td>

</tr>

<tr>

<td>alac</td>

<td>ape</td>

<td>atrac 3</td>

<td>cook</td>

<td>dca</td>

</tr>

<tr>

<td>dsicinaudio</td>

<td>flac</td>

<td>g726</td>

<td>imc</td>

<td>interplay_dpcm</td>

</tr>

<tr>

<td>liba52</td>

<td>libamr_nb</td>

<td>libamr_wb</td>

<td>libfaad</td>

<td>libgsm</td>

</tr>

<tr>

<td>libgsm_ms</td>

<td>mace3</td>

<td>mace6</td>

<td>mp2</td>

<td>mp3</td>

</tr>

<tr>

<td>mp3adu</td>

<td>mp3on4</td>

<td>mpc sv7</td>

<td>mpc sv8</td>

<td>mpeg4aac</td>

</tr>

<tr>

<td>nellymoser</td>

<td>pcm_alaw</td>

<td>pcm_mulaw</td>

<td>pcm_s16be</td>

<td>pcm_s16le</td>

</tr>

<tr>

<td>pcm_s16le_planar</td>

<td>pcm_s24be</td>

<td>pcm_s24daud</td>

<td>pcm_s24le</td>

<td>pcm_s32be</td>

</tr>

<tr>

<td>pcm_s32le</td>

<td>pcm_s8</td>

<td>pcm_u16be</td>

<td>pcm_u16le</td>

<td>pcm_u24be</td>

</tr>

<tr>

<td>pcm_u24le</td>

<td>pcm_u32be</td>

<td>pcm_u32le</td>

<td>pcm_u8</td>

<td>pcm_zork</td>

</tr>

<tr>

<td>qdm2</td>

<td>real_144</td>

<td>real_288</td>

<td>roq_dpcm</td>

<td>shorten</td>

</tr>

<tr>

<td>smackaud</td>

<td>sol_dpcm</td>

<td>sonic</td>

<td>truespeech</td>

<td>tta</td>

</tr>

<tr>

<td>vmdaudio</td>

<td>vorbis</td>

<td>wavpack</td>

<td>wmav1</td>

<td>wmav2</td>

</tr>

<tr>

<td>ws_snd1</td>

<td>xan_dpcm</td>

<td> </td>

<td> </td>

<td> </td>

</tr>

</tbody>

</table>

## Audio encoders

<table>

<tbody>

<tr>

<td>ac3</td>

<td>adpcm_adx</td>

<td>adpcm_ima_wav</td>

<td>adpcm_ms</td>

<td>adpcm_swf</td>

</tr>

<tr>

<td>adpcm_yamaha</td>

<td>flac</td>

<td>g726</td>

<td>libamr_nb</td>

<td>libamr_wb</td>

</tr>

<tr>

<td>libfaac</td>

<td>libgsm</td>

<td>libgsm_ms</td>

<td>libmp3lame</td>

<td>libvorbis</td>

</tr>

<tr>

<td>mp2</td>

<td>pcm_alaw</td>

<td>pcm_mulaw</td>

<td>pcm_s16be</td>

<td>pcm_s16le</td>

</tr>

<tr>

<td>pcm_s24be</td>

<td>pcm_s24daud</td>

<td>pcm_s24le</td>

<td>pcm_s32be</td>

<td>pcm_s32le</td>

</tr>

<tr>

<td>pcm_s8</td>

<td>pcm_u16be</td>

<td>pcm_u16le</td>

<td>pcm_u24be</td>

<td>pcm_u24le</td>

</tr>

<tr>

<td>pcm_u32be</td>

<td>pcm_u32le</td>

<td>pcm_u8</td>

<td>pcm_zork</td>

<td>roq_dpcm</td>

</tr>

<tr>

<td>sonic</td>

<td>sonicls</td>

<td>vorbis</td>

<td>wmav1</td>

<td>wmav2</td>

</tr>

</tbody>

</table>

## Video decoders

<table>

<tbody>

<tr>

<td>4xm</td>

<td>8bps</td>

<td>VMware video</td>

<td>aasc</td>

<td>amv</td>

</tr>

<tr>

<td>asv1</td>

<td>asv2</td>

<td>avs</td>

<td>bethsoftvid</td>

<td>bmp</td>

</tr>

<tr>

<td>c93</td>

<td>camstudio</td>

<td>camtasia</td>

<td>cavs</td>

<td>cinepak</td>

</tr>

<tr>

<td>cljr</td>

<td>cyuv</td>

<td>dnxhd</td>

<td>dsicinvideo</td>

<td>dvvideo</td>

</tr>

<tr>

<td>dxa</td>

<td>ffv1</td>

<td>ffvhuff</td>

<td>flashsv</td>

<td>flic</td>

</tr>

<tr>

<td>flv</td>

<td>fraps</td>

<td>gif</td>

<td>h261</td>

<td>h263</td>

</tr>

<tr>

<td>h263i</td>

<td>h264</td>

<td>huffyuv</td>

<td>idcinvideo</td>

<td>indeo2</td>

</tr>

<tr>

<td>indeo3</td>

<td>interplayvideo</td>

<td>jpegls</td>

<td>kmvc</td>

<td>loco</td>

</tr>

<tr>

<td>mdec</td>

<td>mjpeg</td>

<td>mjpegb</td>

<td>mmvideo</td>

<td>mpeg1video</td>

</tr>

<tr>

<td>mpeg2video</td>

<td>mpeg4</td>

<td>mpegvideo</td>

<td>msmpeg4</td>

<td>msmpeg4v1</td>

</tr>

<tr>

<td>msmpeg4v2</td>

<td>msrle</td>

<td>msvideo1</td>

<td>mszh</td>

<td>nuv</td>

</tr>

<tr>

<td>pam</td>

<td>pbm</td>

<td>pgm</td>

<td>pgmyuv</td>

<td>png</td>

</tr>

<tr>

<td>ppm</td>

<td>ptx</td>

<td>qdraw</td>

<td>qpeg</td>

<td>qtrle</td>

</tr>

<tr>

<td>rawvideo</td>

<td>roqvideo</td>

<td>rpza</td>

<td>rv10</td>

<td>rv20</td>

</tr>

<tr>

<td>sgi</td>

<td>smackvid</td>

<td>smc</td>

<td>snow</td>

<td>sp5x</td>

</tr>

<tr>

<td>svq1</td>

<td>svq3</td>

<td>targa</td>

<td>theora</td>

<td>thp</td>

</tr>

<tr>

<td>tiertexseqvideo</td>

<td>tiff</td>

<td>truemotion1</td>

<td>truemotion2</td>

<td>txd</td>

</tr>

<tr>

<td>ultimotion</td>

<td>vb</td>

<td>vc1</td>

<td>vcr1</td>

<td>vmdvideo</td>

</tr>

<tr>

<td>vp3</td>

<td>vp5</td>

<td>vp6</td>

<td>vp6a</td>

<td>vp6f</td>

</tr>

<tr>

<td>vqavideo</td>

<td>wmv1</td>

<td>wmv2</td>

<td>wmv3</td>

<td>wnv1</td>

</tr>

<tr>

<td>xan_wc3</td>

<td>xl</td>

<td>zlib</td>

<td>zmbv</td>

<td> </td>

</tr>

</tbody>

</table>

## Video encoders

<table>

<tbody>

<tr>

<td>asv1</td>

<td>asv2</td>

<td>bmp</td>

<td>dnxhd</td>

<td>dvvideo</td>

</tr>

<tr>

<td>ffv1</td>

<td>ffvhuff</td>

<td>flashsv</td>

<td>flv</td>

<td>gif</td>

</tr>

<tr>

<td>h261</td>

<td>h263</td>

<td>h263p</td>

<td>huffyuv</td>

<td>jpegls</td>

</tr>

<tr>

<td>libtheora</td>

<td>libx264</td>

<td>libxvid</td>

<td>ljpeg</td>

<td>mjpeg</td>

</tr>

<tr>

<td>mpeg1video</td>

<td>mpeg2video</td>

<td>mpeg4</td>

<td>msmpeg4</td>

<td>msmpeg4v1</td>

</tr>

<tr>

<td>msmpeg4v2</td>

<td>pam</td>

<td>pbm</td>

<td>pgm</td>

<td>pgmyuv</td>

</tr>

<tr>

<td>png</td>

<td>ppm</td>

<td>qtrle</td>

<td>rawvideo</td>

<td>roqvideo</td>

</tr>

<tr>

<td>rv10</td>

<td>rv20</td>

<td>sgi</td>

<td>snow</td>

<td>svq1</td>

</tr>

<tr>

<td>targa</td>

<td>tiff</td>

<td>wmv1</td>

<td>wmv2</td>

<td>zlib</td>

</tr>

<tr>

<td>zmbv</td>

<td> </td>

<td> </td>

<td> </td>

<td> </td>

</tr>

</tbody>

</table>

<a name="10"></a>

# Examples

From a generic AVI to a youtube-like FLV movie, with an embedded MP3 audio stream:

```java
File source = new File("source.avi");  
File target = new File("target.flv");  
AudioAttributes audio = new AudioAttributes();  
audio.setCodec("libmp3lame");  
audio.setBitRate(new Integer(64000));  
audio.setChannels(new Integer(1));  
audio.setSamplingRate(new Integer(22050));  
VideoAttributes video = new VideoAttributes();  
video.setCodec("flv");  
video.setBitRate(new Integer(160000));  
video.setFrameRate(new Integer(15));  
video.setSize(new VideoSize(400, 300));  
EncodingAttributes attrs = new EncodingAttributes();  
attrs.setFormat("flv");  
attrs.setAudioAttributes(audio);  
attrs.setVideoAttributes(video);  
Encoder encoder = new Encoder();  
encoder.encode(source, target, attrs);
```

Next lines extracts audio informations from an AVI and store them in a plain WAV file:

```java
File source = new File("source.avi");  
File target = new File("target.wav");  
AudioAttributes audio = new AudioAttributes();  
audio.setCodec("pcm_s16le");  
EncodingAttributes attrs = new EncodingAttributes();  
attrs.setFormat("wav");  
attrs.setAudioAttributes(audio);  
Encoder encoder = new Encoder();  
encoder.encode(source, target, attrs);
```

Next example takes an audio WAV file and generates a 128 kbit/s, stereo, 44100 Hz MP3 file:

```java
File source = new File("source.wav");  
File target = new File("target.mp3");  
AudioAttributes audio = new AudioAttributes();  
audio.setCodec("libmp3lame");  
audio.setBitRate(new Integer(128000));  
audio.setChannels(new Integer(2));  
audio.setSamplingRate(new Integer(44100));  
EncodingAttributes attrs = new EncodingAttributes();  
attrs.setFormat("mp3");  
attrs.setAudioAttributes(audio);  
Encoder encoder = new Encoder();  
encoder.encode(source, target, attrs);
```

Next one decodes a generic AVI file and creates another one with the same video stream of the source and a re-encoded low quality MP3 audio stream:

```java
File source = new File("source.avi");  
File target = new File("target.avi");  
AudioAttributes audio = new AudioAttributes();  
audio.setCodec("libmp3lame");  
audio.setBitRate(new Integer(56000));  
audio.setChannels(new Integer(1));  
audio.setSamplingRate(new Integer(22050));  
VideoAttributes video = new VideoAttributes();  
video.setCodec(VideoAttributes.DIRECT_STREAM_COPY);  
EncodingAttributes attrs = new EncodingAttributes();  
attrs.setFormat("avi");  
attrs.setAudioAttributes(audio);  
attrs.setVideoAttributes(video);  
Encoder encoder = new Encoder();  
encoder.encode(source, target, attrs);
```

Next one generates an AVI with MPEG 4/DivX video and OGG Vorbis audio:

```java
File source = new File("source.avi");  
File target = new File("target.avi");  
AudioAttributes audio = new AudioAttributes();  
audio.setCodec("libvorbis");  
VideoAttributes video = new VideoAttributes();  
video.setCodec("mpeg4");  
video.setTag("DIVX");  
video.setBitRate(new Integer(160000));  
video.setFrameRate(new Integer(30));  
EncodingAttributes attrs = new EncodingAttributes();  
attrs.setFormat("mpegvideo");  
attrs.setAudioAttributes(audio);  
attrs.setVideoAttributes(video);  
Encoder encoder = new Encoder();  
encoder.encode(source, target, attrs);
```

A smartphone suitable video:

```
File source = new File("source.avi");  
File target = new File("target.3gp");  
AudioAttributes audio = new AudioAttributes();  
audio.setCodec("libfaac");  
audio.setBitRate(new Integer(128000));  
audio.setSamplingRate(new Integer(44100));  
audio.setChannels(new Integer(2));  
VideoAttributes video = new VideoAttributes();  
video.setCodec("mpeg4");  
video.setBitRate(new Integer(160000));  
video.setFrameRate(new Integer(15));  
video.setSize(new VideoSize(176, 144));  
EncodingAttributes attrs = new EncodingAttributes();  
attrs.setFormat("3gp");  
attrs.setAudioAttributes(audio);  
attrs.setVideoAttributes(video);  
Encoder encoder = new Encoder();  
encoder.encode(source, target, attrs);
```
