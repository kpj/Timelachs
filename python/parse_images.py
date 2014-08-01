import os, re, sys


def execute(cmd):
    print('Executing: "%s"' % cmd)
    return os.system(cmd)

def rotate(image):
    return execute('convert "%s" -rotate -90 "%s"' % (image, image))

def process_images(folder_name):
    for img in os.listdir(folder_name):
        execute('convert "%s/%s" -rotate -90 "%s/%s"' % (folder_name, img, folder_name, img))

def create_gif(folder_name, output, delay=4):
    return execute('convert -delay %i -loop 0 "%s/img_*" "%s/%s"' % (delay, folder_name, folder_name, output))

def create_video(folder_name, output, framerate=25):
    for f in os.listdir(folder_name):
        if f.startswith('img_'):
            photo_num = str(len(re.match('img_([0-9]*).jpg', f).groups(0)[0]))

    return execute(('ffmpeg -r %i -i "%s' % (framerate, folder_name)) + '/img_%0' + photo_num + 'd.jpg"' + (' "%s/%s"' % (folder_name, output)))


if __name__ == '__main__':
    if len(sys.argv) != 2:
        print('Usage: %s <folder>' % sys.argv[0])
        sys.exit(1)

    folder = sys.argv[1]

    create_gif(folder, 'foo.gif')
    create_video(folder, 'foo.mp4')