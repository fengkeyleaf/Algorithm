"use strict"

/*
 * Slider.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 Basic operations on 12/8/2021$
 */

/**
 * class of interactive slider
 *
 * reference resource:
 * https://www.cnblogs.com/ypppt/p/13326409.html
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class Slider {

    /**
     * @param {String} id bounded div
     * @param {String} nameBar
     * @param {String} nameThumb
     * @param {Function} handler
     */

    constructor( id, nameBar, nameThumb, handler ) {
        this.slider = document.getElementById( id );
        this.bar = this.slider.querySelector( nameBar );
        this.thumb = this.slider.querySelector( nameThumb );

        this.handler = handler;
        this.per = 0;

        this.addEvents();
        this.setProgress();
    }

    static getStyle( obj, styleName ) {
        if ( obj.currentStyle ) {
            return obj.currentStyle[ styleName ];
        } else {
            return getComputedStyle( obj, null )[ styleName ];
        }
    }

    addEvents() {
        let self = this;
        self.slider.addEventListener( "mousedown", sliderEvent );
        document.addEventListener( "mousemove", documentEventMouseMove );
        document.addEventListener( "mouseup", documentEventMouseUp );

        function sliderEvent( e ) {
            if ( e.button === 0 ) { // left click
                self.mDown = true;
                self.beginX = e.offsetX;
                self.positionX = e.offsetX;
                self.beginClientX = e.clientX;
                self.sliderLong = parseInt( Slider.getStyle( self.slider, 'width' ) );
                self.setProgress( parseInt( self.positionX / self.sliderLong * 100 ) );

                self.handler();
            }
        }

        function documentEventMouseMove( e ) {
            if ( self.mDown ) {
                let moveX = e.clientX - self.beginClientX;
                self.positionX = ( self.beginX + moveX > self.sliderLong ) ? self.sliderLong : ( self.beginX + moveX < 0 ) ? 0 : self.beginX + moveX;
                self.setProgress( parseInt( self.positionX / self.sliderLong * 100 ) );

                self.handler();
            }
        }

        function documentEventMouseUp( e ) {
            if ( e.button === 0 ) {
                self.mDown = false;
            }
        }
    }

    setProgress( pre = 0 ) {
        this.bar.style.width = ( this.per = pre ) + '%';
    }

    getProgress() {
        return this.per;
    }
}