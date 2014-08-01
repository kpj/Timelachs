import os, os.path
import datetime
import shutil
import asyncore, socket
import threading, struct


class ImageHandler(object):
    def __init__(self, ifolder, fnum=5):
        self.folder_name = ifolder
        self.photo_num = fnum

        # remove previous projects
        try:
            shutil.rmtree(self.folder_name)
        except FileNotFoundError:
            pass
        finally:
            os.mkdir(self.folder_name)

    def add_image(self, image):
        """Returns number of image on success, -1 on failure
        """
        try:
            os.rename(image, ('%s/img_%0' + str(self.photo_num+1) + 'd.jpg') % (self.folder_name, self.get_image_number()+1))
            return self.get_image_number()
        except FileNotFoundError:
            return -1

    def get_image_number(self):
        return len(os.listdir(self.folder_name))

class Client(asyncore.dispatcher):
    def __init__(self, sock):
        super().__init__(sock)
        self.sock = sock
        self.imgh = image_handler = ImageHandler(datetime.datetime.now().strftime('%Y%m%d%H%M%S') + '_result')

        self.init_len = 4 # number of bytes payload length is stored in
        self.recv_len = 8192 # bytes read per step during payload reception

        self.reset()

    def reset(self):
        self.init = True # receiving payload size
        self.data = bytes() # receiving payload
        self.size = -1

    def handle_read(self):
        if self.init:
            res = self.recv(self.init_len)
            self.size = struct.unpack('!i', res)[0]
            self.init = False
        else:
            chunk = self.recv(self.recv_len)

            if chunk:
                self.data += chunk
                if len(self.data) == self.size:
                    print("Saving image")
                    self.bytes2image(self.data)
                    self.reset()

    def bytes2image(self, byts):
        name = 'current.jpg'
        with open(name, 'wb') as fd:
            fd.write(byts)
        self.imgh.add_image(name)

class Server(asyncore.dispatcher):
    def __init__(self, host, port):
        asyncore.dispatcher.__init__(self)
        self.create_socket(socket.AF_INET, socket.SOCK_STREAM)
        self.set_reuse_addr()
        self.bind((host, port))
        self.listen(5)

    def handle_accepted(self, sock, addr):
        print('Incoming connection from %s' % repr(addr))
        client = Client(sock)


if __name__ == '__main__':
    server = Server('0.0.0.0', 50505)
    server = threading.Thread(target = asyncore.loop)
    server.start()

    print('Started server')