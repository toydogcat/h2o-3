package water.fvec.persist;

import water.*;
import water.fvec.Chunk;
import water.fvec.Frame;
import water.fvec.Vec;
import water.util.FileUtils;

import java.net.URI;

import static water.fvec.persist.PersistUtils.*;

public class FramePersist {
    
    static {
        TypeMap.onIce(FrameMeta.class.getName());
    }

    private final Frame frame;

    public FramePersist(Frame frame) {
        this.frame = frame;
    }
    
    private static class FrameMeta extends Iced<FrameMeta> {
        Key<Frame> key;
        String[] names;
        Key<Vec>[] keys;
        Vec[] vecs;
        
        FrameMeta(Frame f) {
            key = f._key;
            names = f.names();
            keys = f.keys();
            vecs = f.vecs();
        }
    }

    private static URI getMetaUri(Key key, String dest) {
        return FileUtils.getURI(dest + "/" + key);
    }

    private static URI getDataUri(String metaUri, int cidx) {
        return FileUtils.getURI(metaUri + "_" + H2O.SELF.index() + "_" + cidx);
    }

    public void saveTo(String uri) {
        uri = sanitizeUri(uri);
        URI metaUri = getMetaUri(frame._key, uri);
        write(metaUri, ab -> {
            ab.put(new FrameMeta(frame));
        });
        new SaveChunksTask(metaUri.toString()).doAll(frame).join();
    }

    static class SaveChunksTask extends MRTask<SaveChunksTask> {

        private final String metaUri;

        SaveChunksTask(String metaUri) {
            this.metaUri = metaUri;
        }

        @Override
        public void map(Chunk[] cs) {
            PersistUtils.write(getDataUri(metaUri, cs[0].cidx()), ab -> writeChunks(ab, cs));
        }

        private void writeChunks(AutoBuffer autoBuffer, Chunk[] chunks) {
            for (Chunk c : chunks) {
                autoBuffer.put(c);
            }
        }
    }

    public static Frame loadFrom(Key<Frame> key, String uri) {
        uri = sanitizeUri(uri);
        URI metaUri = getMetaUri(key, uri);
        FrameMeta meta = read(metaUri, AutoBuffer::get);
        Vec con = Vec.makeConN(meta.vecs[0].length(), meta.vecs[0].nChunks());
        new LoadChunksTask(metaUri.toString(), meta.vecs).doAll(con);
        for (Vec v : meta.vecs) DKV.put(v);
        Frame frame = new Frame(meta.key, meta.names, meta.vecs);
        DKV.put(frame);
        return frame;
    }

    static class LoadChunksTask extends MRTask<LoadChunksTask> {

        private final String metaUri;
        private final Vec[] vecs;

        LoadChunksTask(String metaUri, Vec[] vecs) {
            this.metaUri = metaUri;
            this.vecs = vecs;
        }

        @Override
        public void map(Chunk c) {
            PersistUtils.read(getDataUri(metaUri, c.cidx()), ab -> readChunks(ab, c.cidx()));
        }

        private int readChunks(AutoBuffer autoBuffer, int cidx) {
            for (Vec v : vecs) {
                DKV.put(v.chunkKey(cidx), new Value(v.chunkKey(cidx), autoBuffer.get()));
            }
            return vecs.length;
        }
    }

}
