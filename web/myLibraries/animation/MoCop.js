"use strict"

/*
 * MoCop.js
 *
 * Version:
 *     $1.0$
 *
 * Revisions:
 *     $1.0 setUpShapes(), orientBone() on 11/01/2021$
 */

/**
 * This class consists exclusively of static methods
 * that related to Motion Capture
 *
 * @author       Xiaoyu Tongyang, or call me sora for short
 */

export default class MoCop {
    static materialSphere = new THREE.MeshLambertMaterial( {
        color: 0x0000ff // blue
    } );
    static materialLine = new THREE.MeshLambertMaterial( {
        color: 0xCC0066 // red
    } );
    static radius = 0.5;
    static height = 5;
    static splits = 20;
    static groups = [];

    /**
     * set up Articulated Figures from the Skeleton in three.js
     *
     * @param {Bone} bone
     * @param {Scene} scene
     */

    static setupShapes( bone, scene ) {
        let joint = new THREE.SphereGeometry( MoCop.radius, MoCop.splits, MoCop.splits );
        let mesh = new THREE.Mesh( joint, MoCop.materialSphere );
        mesh.position.set( bone.position.x, bone.position.y, bone.position.z );
        mesh.matrixAutoUpdate = false;

        let root = {
            name: bone.name,
            joint: mesh,
            offset: bone.position.clone(),
            children: [],
            parent: null
        };
        MoCop.groups.push( root );

        scene.add( mesh );
        //     for each child c of the bone
        for ( let c of bone.children ) {
            if ( c.parent == null ) continue;
            //         orientBone (c);
            root.children.push( MoCop.__setupShapes( c, root, scene ) );
            root.children.getLast().parent = root;
        }

        return root;
    }

    /**
     *
     * @param {Bone} bone
     * @param {{}} parent
     * @param {Scene} scene
     */

    static __setupShapes( bone, parent, scene ) {
        // console.log(parent.joint);
        // T <- get parent translation from parent's mesh
        // let parentSpherePo = parent.joint.position.clone();

        const points = [];
        points.push( new THREE.Vector3( 0, 0, 0 ) );
        points.push( new THREE.Vector3( 0, 0, 0 ) );
        const geometry = new THREE.BufferGeometry().setFromPoints( points );
        let line = new THREE.Line( geometry, MoCop.materialLine );

        let joint = new THREE.SphereGeometry( MoCop.radius, MoCop.splits, MoCop.splits );
        // joint = new THREE.CylinderGeometry( MoCop.radius, MoCop.radius, MoCop.height, 32 );
        let mesh = new THREE.Mesh( joint, MoCop.materialSphere );
        mesh.position.set( bone.position.x, bone.position.y, bone.position.z );
        mesh.matrixAutoUpdate = false;

        scene.add( mesh );
        scene.add( line );
        let root = {
            name: bone.name,
            joint: mesh,
            line: line,
            children: [],
            parent: null
        };
        MoCop.groups.push( root );

        // for each child c of the bone
        for ( let c of bone.children ) {
            if ( c.parent == null ) continue;
            // orientBone (c);
            root.children.push(MoCop.__setupShapes( c, root, scene ) );
            root.children.getLast().parent = root;
        }

        return root;
    }

    /**
     * key keyframes from AnimationClip class
     *
     * @param {AnimationClip} clip
     */

    static getKeyFrames( clip) {
        let res = [];
        console.assert( clip.tracks.length % 2 === 0 );

        for ( let i = 0; i < clip.tracks.length; i += 2 ) {
            let keys = MoCop.convertingKeyframe( clip.tracks[ i ], clip.tracks[ i + 1 ] );
            res.push( keys );
        }

        let keyframes = [];
        for ( let j = 0; j < res[ 0 ].length; j++ ) {
            let keyframe = [];
            for ( let i = 0; i < res.length; i++ ) {
                keyframe.push( res[ i ][ j ] );
            }

            keyframes.push( keyframe );
        }

        // first group of keyframes is keyframes[ 0 ],
        // first keyframe is keyframes[ i ][ 0 ]
        return keyframes;
    }

    /**
     * get keyframes from the AnimationClip in three.js
     *
     * @param {VectorKeyframeTrack} trackT
     * @param {QuaternionKeyframeTrack} trackQ
     */

    static convertingKeyframe( trackT, trackQ ) {
        let t = MoCop.__convertingKeyframeVector3( trackT );
        let q = MoCop.__convertingKeyframeQuaternion( trackQ );
        console.assert( t.length === q.length );
        console.assert( t.name === q.name );
        let res = [];
        for ( let i = 0; i < t.length; i++ ) {
            console.assert( t[ i ].time === q[ i ].time );
            res.push( {
                name: t[ i ].name,
                time: t[ i ].time,
                position: t[ i ].position,
                quaternion: q[ i ].quaternion
            } );
        }

        return res;
    }

    /**
     *
     * @param {VectorKeyframeTrack} track
     */

    static __convertingKeyframeVector3( track ) {
        let n = 0;
        let res = [];
        for ( let t of track.times ) {
            let q = track.values.slice( n, n + 3 );
            console.assert( q.length === 3 );
            n += 3;
            res.push( {
                name: track.name,
                time: t * 1000, // ms
                position: new THREE.Vector3( q[ 0 ], q[ 1 ], q[ 2 ] )
            } );
        }

        return res;
    }

    /**
     *
     * @param {QuaternionKeyframeTrack} track
     */

    static __convertingKeyframeQuaternion( track ) {
        let n = 0;
        let res = [];
        for ( let t of track.times ) {
            let q = track.values.slice( n, n + 4 );
            console.assert( q.length === 4 );
            n += 4;
            res.push( {
                name: track.name,
                time: t * 1000, // ms
                quaternion: new THREE.Quaternion( q[ 0 ], q[ 1 ], q[ 2 ], q[ 3 ] )
            } );
        }

        return res;
    }

    /**
     * animate Hierarchical Models
     *
     * @param {Scene} scene
     * @param {{}} root
     * @param {Array} keyframes
     */

    static orientBone( scene, root, keyframes ) {
        console.assert( root.parent == null );
        // Xposition Yposition Zposition Zrotation Xrotation Yrotation

        let keyframe = keyframes.pop();
        console.assert( keyframe != null, root );
        console.assert( keyframe.name, keyframe );
        console.assert( keyframe.name.includes( root.name ), keyframe.name, root.name );
        let translation = new THREE.Vector3();
        translation.add( keyframe.position );
        translation.add( root.offset );

        root.joint.position.set( translation.x, translation.y, translation.z );
        root.joint.matrix.compose( root.joint.position, keyframe.quaternion, root.joint.scale );
        root.joint.matrixWorld.copy( root.joint.matrix );

        // for each child c of the root
        for ( let c of root.children ) {
            // orientBone (c);
            MoCop.__orientBone( scene, c, keyframes );
        }

        root.joint.position.set( root.offset.x, root.offset.y, root.offset.z );
    }

    /**
     *
     * @param {Scene} scene
     * @param {{}} root
     * @param {Array} keyframes
     */

    static __orientBone( scene, root,  keyframes ) {
        if ( root == null ) return;

        let keyframe = root.name === "ENDSITE" ? null : keyframes.pop();
        let q = keyframe == null ? new THREE.Quaternion() : keyframe.quaternion;
        console.assert( keyframe != null && root.name !== "ENDSITE" || keyframe == null && root.name === "ENDSITE", root, keyframe );
        if ( keyframe != null )
            console.assert( keyframe.name.includes( root.name ), keyframe, root.name );

        // T_bToWorld = T_bToA * T_aToWorld
        let parentMatrixWorld = root.parent.joint.matrixWorld;
        root.joint.matrix.compose( root.joint.position, q, root.joint.scale );
        root.joint.matrixWorld.multiplyMatrices( parentMatrixWorld, root.joint.matrix );

        // set up line connecting joints
        const positionLine = root.line.geometry.getAttribute( 'position' );
        const po = new THREE.Vector3();
        po.setFromMatrixPosition( root.parent.joint.matrixWorld );
        positionLine.setXYZ( 0, po.x, po.y, po.z );
        po.setFromMatrixPosition( root.joint.matrixWorld );
        positionLine.setXYZ( 1, po.x, po.y, po.z );
        positionLine.needsUpdate = true;
        root.line.updateMatrixWorld( true );

        // for each child c of the bone
        for ( let c of root.children ) {
            // orientBone (c);
            MoCop.__orientBone( scene, c, keyframes );
        }
    }
}