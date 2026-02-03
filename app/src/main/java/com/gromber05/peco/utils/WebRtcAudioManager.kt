package com.gromber05.peco.utils

import android.content.Context
import org.webrtc.*

class WebRtcAudioManager(
    private val appContext: Context
) {

    private val eglBase: EglBase = EglBase.create()

    private val peerConnectionFactory: PeerConnectionFactory by lazy {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(appContext)
                .setEnableInternalTracer(false)
                .createInitializationOptions()
        )

        val options = PeerConnectionFactory.Options()

        PeerConnectionFactory.builder()
            .setOptions(options)
            .createPeerConnectionFactory()
    }

    private var peerConnection: PeerConnection? = null
    private var localAudioTrack: AudioTrack? = null

    fun createPeerConnection(
        iceServers: List<PeerConnection.IceServer>,
        observer: PeerConnection.Observer
    ): PeerConnection {
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers).apply {
            // Modo más compatible
            sdpSemantics = PeerConnection.SdpSemantics.UNIFIED_PLAN
            continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy.GATHER_CONTINUALLY
        }

        val pc = peerConnectionFactory.createPeerConnection(rtcConfig, observer)
            ?: error("Failed to create PeerConnection")

        peerConnection = pc
        return pc
    }

    fun startLocalAudio(pc: PeerConnection) {
        val audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        val track = peerConnectionFactory.createAudioTrack("audio0", audioSource)
        localAudioTrack = track

        val stream = peerConnectionFactory.createLocalMediaStream("stream0")
        stream.addTrack(track)
        pc.addStream(stream)
    }

    fun createOffer(pc: PeerConnection, onSdp: (SessionDescription) -> Unit, onError: (String) -> Unit) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
        }

        pc.createOffer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription) {
                pc.setLocalDescription(object : SdpObserver {
                    override fun onSetSuccess() = onSdp(desc)
                    override fun onSetFailure(p0: String) = onError(p0)
                    override fun onCreateSuccess(p0: SessionDescription) {}
                    override fun onCreateFailure(p0: String) {}
                }, desc)
            }

            override fun onCreateFailure(p0: String) = onError(p0)
            override fun onSetSuccess() {}
            override fun onSetFailure(p0: String) {}
        }, constraints)
    }

    fun createAnswer(pc: PeerConnection, onSdp: (SessionDescription) -> Unit, onError: (String) -> Unit) {
        val constraints = MediaConstraints().apply {
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"))
            mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "false"))
        }

        pc.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription) {
                pc.setLocalDescription(object : SdpObserver {
                    override fun onSetSuccess() = onSdp(desc)
                    override fun onSetFailure(p0: String) = onError(p0)
                    override fun onCreateSuccess(p0: SessionDescription) {}
                    override fun onCreateFailure(p0: String) {}
                }, desc)
            }

            override fun onCreateFailure(p0: String) = onError(p0)
            override fun onSetSuccess() {}
            override fun onSetFailure(p0: String) {}
        }, constraints)
    }

    fun setRemoteSdp(pc: PeerConnection, desc: SessionDescription, onOk: () -> Unit, onError: (String) -> Unit) {
        pc.setRemoteDescription(object : SdpObserver {
            override fun onSetSuccess() = onOk()
            override fun onSetFailure(p0: String) = onError(p0)
            override fun onCreateSuccess(p0: SessionDescription) {}
            override fun onCreateFailure(p0: String) {}
        }, desc)
    }

    fun addIceCandidate(pc: PeerConnection, ice: IceCandidate) {
        pc.addIceCandidate(ice)
    }

    fun setMuted(muted: Boolean) {
        localAudioTrack?.setEnabled(!muted)
    }

    fun end() {
        peerConnection?.close()
        peerConnection = null
        localAudioTrack = null
    }
}
