package com.shmet.protobuf.gen;

public final class DeviceRealDataProto {
    private DeviceRealDataProto() {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistryLite registry) {
    }

    public static void registerAllExtensions(
            com.google.protobuf.ExtensionRegistry registry) {
        registerAllExtensions(
                (com.google.protobuf.ExtensionRegistryLite) registry);
    }

    public interface ProtoDeviceRealDataReceivedOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ProtoDeviceRealDataReceived)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        java.util.List<DeviceRealDataProto.ProtoDeviceRealDataInfo>
        getProtoDeviceRealDataInfoList();

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        DeviceRealDataProto.ProtoDeviceRealDataInfo getProtoDeviceRealDataInfo(int index);

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        int getProtoDeviceRealDataInfoCount();

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        java.util.List<? extends DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder>
        getProtoDeviceRealDataInfoOrBuilderList();

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder getProtoDeviceRealDataInfoOrBuilder(
                int index);
    }

    /**
     * Protobuf type {@code ProtoDeviceRealDataReceived}
     */
    public static final class ProtoDeviceRealDataReceived extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ProtoDeviceRealDataReceived)
            ProtoDeviceRealDataReceivedOrBuilder {
        private static final long serialVersionUID = 0L;

        // Use ProtoDeviceRealDataReceived.newBuilder() to construct.
        private ProtoDeviceRealDataReceived(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private ProtoDeviceRealDataReceived() {
            protoDeviceRealDataInfo_ = java.util.Collections.emptyList();
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return this.unknownFields;
        }

        private ProtoDeviceRealDataReceived(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new java.lang.NullPointerException();
            }
            int mutable_bitField0_ = 0;
            com.google.protobuf.UnknownFieldSet.Builder unknownFields =
                    com.google.protobuf.UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        case 10: {
                            if (!((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                                protoDeviceRealDataInfo_ = new java.util.ArrayList<DeviceRealDataProto.ProtoDeviceRealDataInfo>();
                                mutable_bitField0_ |= 0x00000001;
                            }
                            protoDeviceRealDataInfo_.add(
                                    input.readMessage(DeviceRealDataProto.ProtoDeviceRealDataInfo.parser(), extensionRegistry));
                            break;
                        }
                        default: {
                            if (!parseUnknownFieldProto3(
                                    input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                if (((mutable_bitField0_ & 0x00000001) == 0x00000001)) {
                    protoDeviceRealDataInfo_ = java.util.Collections.unmodifiableList(protoDeviceRealDataInfo_);
                }
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return DeviceRealDataProto.internal_static_ProtoDeviceRealDataReceived_descriptor;
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return DeviceRealDataProto.internal_static_ProtoDeviceRealDataReceived_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            DeviceRealDataProto.ProtoDeviceRealDataReceived.class, DeviceRealDataProto.ProtoDeviceRealDataReceived.Builder.class);
        }

        public static final int PROTODEVICEREALDATAINFO_FIELD_NUMBER = 1;
        private java.util.List<DeviceRealDataProto.ProtoDeviceRealDataInfo> protoDeviceRealDataInfo_;

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        public java.util.List<DeviceRealDataProto.ProtoDeviceRealDataInfo> getProtoDeviceRealDataInfoList() {
            return protoDeviceRealDataInfo_;
        }

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        public java.util.List<? extends DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder>
        getProtoDeviceRealDataInfoOrBuilderList() {
            return protoDeviceRealDataInfo_;
        }

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        public int getProtoDeviceRealDataInfoCount() {
            return protoDeviceRealDataInfo_.size();
        }

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        public DeviceRealDataProto.ProtoDeviceRealDataInfo getProtoDeviceRealDataInfo(int index) {
            return protoDeviceRealDataInfo_.get(index);
        }

        /**
         * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
         */
        public DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder getProtoDeviceRealDataInfoOrBuilder(
                int index) {
            return protoDeviceRealDataInfo_.get(index);
        }

        private byte memoizedIsInitialized = -1;

        @java.lang.Override
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            for (int i = 0; i < protoDeviceRealDataInfo_.size(); i++) {
                output.writeMessage(1, protoDeviceRealDataInfo_.get(i));
            }
            unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            for (int i = 0; i < protoDeviceRealDataInfo_.size(); i++) {
                size += com.google.protobuf.CodedOutputStream
                        .computeMessageSize(1, protoDeviceRealDataInfo_.get(i));
            }
            size += unknownFields.getSerializedSize();
            memoizedSize = size;
            return size;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof DeviceRealDataProto.ProtoDeviceRealDataReceived)) {
                return super.equals(obj);
            }
            DeviceRealDataProto.ProtoDeviceRealDataReceived other = (DeviceRealDataProto.ProtoDeviceRealDataReceived) obj;

            boolean result = true;
            result = result && getProtoDeviceRealDataInfoList()
                    .equals(other.getProtoDeviceRealDataInfoList());
            result = result && unknownFields.equals(other.unknownFields);
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            if (getProtoDeviceRealDataInfoCount() > 0) {
                hash = (37 * hash) + PROTODEVICEREALDATAINFO_FIELD_NUMBER;
                hash = (53 * hash) + getProtoDeviceRealDataInfoList().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(DeviceRealDataProto.ProtoDeviceRealDataReceived prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @java.lang.Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code ProtoDeviceRealDataReceived}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ProtoDeviceRealDataReceived)
                DeviceRealDataProto.ProtoDeviceRealDataReceivedOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return DeviceRealDataProto.internal_static_ProtoDeviceRealDataReceived_descriptor;
            }

            @java.lang.Override
            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return DeviceRealDataProto.internal_static_ProtoDeviceRealDataReceived_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                DeviceRealDataProto.ProtoDeviceRealDataReceived.class, DeviceRealDataProto.ProtoDeviceRealDataReceived.Builder.class);
            }

            // Construct using DeviceRealDataProto.ProtoDeviceRealDataReceived.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                    getProtoDeviceRealDataInfoFieldBuilder();
                }
            }

            @java.lang.Override
            public Builder clear() {
                super.clear();
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    protoDeviceRealDataInfo_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000001);
                } else {
                    protoDeviceRealDataInfoBuilder_.clear();
                }
                return this;
            }

            @java.lang.Override
            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return DeviceRealDataProto.internal_static_ProtoDeviceRealDataReceived_descriptor;
            }

            @java.lang.Override
            public DeviceRealDataProto.ProtoDeviceRealDataReceived getDefaultInstanceForType() {
                return DeviceRealDataProto.ProtoDeviceRealDataReceived.getDefaultInstance();
            }

            @java.lang.Override
            public DeviceRealDataProto.ProtoDeviceRealDataReceived build() {
                DeviceRealDataProto.ProtoDeviceRealDataReceived result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            @java.lang.Override
            public DeviceRealDataProto.ProtoDeviceRealDataReceived buildPartial() {
                DeviceRealDataProto.ProtoDeviceRealDataReceived result = new DeviceRealDataProto.ProtoDeviceRealDataReceived(this);
                int from_bitField0_ = bitField0_;
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    if (((bitField0_ & 0x00000001) == 0x00000001)) {
                        protoDeviceRealDataInfo_ = java.util.Collections.unmodifiableList(protoDeviceRealDataInfo_);
                        bitField0_ = (bitField0_ & ~0x00000001);
                    }
                    result.protoDeviceRealDataInfo_ = protoDeviceRealDataInfo_;
                } else {
                    result.protoDeviceRealDataInfo_ = protoDeviceRealDataInfoBuilder_.build();
                }
                onBuilt();
                return result;
            }

            @java.lang.Override
            public Builder clone() {
                return (Builder) super.clone();
            }

            @java.lang.Override
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return (Builder) super.setField(field, value);
            }

            @java.lang.Override
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @java.lang.Override
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @java.lang.Override
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, java.lang.Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @java.lang.Override
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @java.lang.Override
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof DeviceRealDataProto.ProtoDeviceRealDataReceived) {
                    return mergeFrom((DeviceRealDataProto.ProtoDeviceRealDataReceived) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(DeviceRealDataProto.ProtoDeviceRealDataReceived other) {
                if (other == DeviceRealDataProto.ProtoDeviceRealDataReceived.getDefaultInstance()) return this;
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    if (!other.protoDeviceRealDataInfo_.isEmpty()) {
                        if (protoDeviceRealDataInfo_.isEmpty()) {
                            protoDeviceRealDataInfo_ = other.protoDeviceRealDataInfo_;
                            bitField0_ = (bitField0_ & ~0x00000001);
                        } else {
                            ensureProtoDeviceRealDataInfoIsMutable();
                            protoDeviceRealDataInfo_.addAll(other.protoDeviceRealDataInfo_);
                        }
                        onChanged();
                    }
                } else {
                    if (!other.protoDeviceRealDataInfo_.isEmpty()) {
                        if (protoDeviceRealDataInfoBuilder_.isEmpty()) {
                            protoDeviceRealDataInfoBuilder_.dispose();
                            protoDeviceRealDataInfoBuilder_ = null;
                            protoDeviceRealDataInfo_ = other.protoDeviceRealDataInfo_;
                            bitField0_ = (bitField0_ & ~0x00000001);
                            protoDeviceRealDataInfoBuilder_ =
                                    com.google.protobuf.GeneratedMessageV3.alwaysUseFieldBuilders ?
                                            getProtoDeviceRealDataInfoFieldBuilder() : null;
                        } else {
                            protoDeviceRealDataInfoBuilder_.addAllMessages(other.protoDeviceRealDataInfo_);
                        }
                    }
                }
                this.mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @java.lang.Override
            public final boolean isInitialized() {
                return true;
            }

            @java.lang.Override
            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                DeviceRealDataProto.ProtoDeviceRealDataReceived parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (DeviceRealDataProto.ProtoDeviceRealDataReceived) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            private java.util.List<DeviceRealDataProto.ProtoDeviceRealDataInfo> protoDeviceRealDataInfo_ =
                    java.util.Collections.emptyList();

            private void ensureProtoDeviceRealDataInfoIsMutable() {
                if (!((bitField0_ & 0x00000001) == 0x00000001)) {
                    protoDeviceRealDataInfo_ = new java.util.ArrayList<DeviceRealDataProto.ProtoDeviceRealDataInfo>(protoDeviceRealDataInfo_);
                    bitField0_ |= 0x00000001;
                }
            }

            private com.google.protobuf.RepeatedFieldBuilderV3<
                    DeviceRealDataProto.ProtoDeviceRealDataInfo, DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder, DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder> protoDeviceRealDataInfoBuilder_;

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public java.util.List<DeviceRealDataProto.ProtoDeviceRealDataInfo> getProtoDeviceRealDataInfoList() {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    return java.util.Collections.unmodifiableList(protoDeviceRealDataInfo_);
                } else {
                    return protoDeviceRealDataInfoBuilder_.getMessageList();
                }
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public int getProtoDeviceRealDataInfoCount() {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    return protoDeviceRealDataInfo_.size();
                } else {
                    return protoDeviceRealDataInfoBuilder_.getCount();
                }
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public DeviceRealDataProto.ProtoDeviceRealDataInfo getProtoDeviceRealDataInfo(int index) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    return protoDeviceRealDataInfo_.get(index);
                } else {
                    return protoDeviceRealDataInfoBuilder_.getMessage(index);
                }
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder setProtoDeviceRealDataInfo(
                    int index, DeviceRealDataProto.ProtoDeviceRealDataInfo value) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureProtoDeviceRealDataInfoIsMutable();
                    protoDeviceRealDataInfo_.set(index, value);
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.setMessage(index, value);
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder setProtoDeviceRealDataInfo(
                    int index, DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder builderForValue) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    ensureProtoDeviceRealDataInfoIsMutable();
                    protoDeviceRealDataInfo_.set(index, builderForValue.build());
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.setMessage(index, builderForValue.build());
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder addProtoDeviceRealDataInfo(DeviceRealDataProto.ProtoDeviceRealDataInfo value) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureProtoDeviceRealDataInfoIsMutable();
                    protoDeviceRealDataInfo_.add(value);
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.addMessage(value);
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder addProtoDeviceRealDataInfo(
                    int index, DeviceRealDataProto.ProtoDeviceRealDataInfo value) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    ensureProtoDeviceRealDataInfoIsMutable();
                    protoDeviceRealDataInfo_.add(index, value);
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.addMessage(index, value);
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder addProtoDeviceRealDataInfo(
                    DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder builderForValue) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    ensureProtoDeviceRealDataInfoIsMutable();
                    protoDeviceRealDataInfo_.add(builderForValue.build());
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.addMessage(builderForValue.build());
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder addProtoDeviceRealDataInfo(
                    int index, DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder builderForValue) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    ensureProtoDeviceRealDataInfoIsMutable();
                    protoDeviceRealDataInfo_.add(index, builderForValue.build());
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.addMessage(index, builderForValue.build());
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder addAllProtoDeviceRealDataInfo(
                    java.lang.Iterable<? extends DeviceRealDataProto.ProtoDeviceRealDataInfo> values) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    ensureProtoDeviceRealDataInfoIsMutable();
                    com.google.protobuf.AbstractMessageLite.Builder.addAll(
                            values, protoDeviceRealDataInfo_);
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.addAllMessages(values);
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder clearProtoDeviceRealDataInfo() {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    protoDeviceRealDataInfo_ = java.util.Collections.emptyList();
                    bitField0_ = (bitField0_ & ~0x00000001);
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.clear();
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public Builder removeProtoDeviceRealDataInfo(int index) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    ensureProtoDeviceRealDataInfoIsMutable();
                    protoDeviceRealDataInfo_.remove(index);
                    onChanged();
                } else {
                    protoDeviceRealDataInfoBuilder_.remove(index);
                }
                return this;
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder getProtoDeviceRealDataInfoBuilder(
                    int index) {
                return getProtoDeviceRealDataInfoFieldBuilder().getBuilder(index);
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder getProtoDeviceRealDataInfoOrBuilder(
                    int index) {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    return protoDeviceRealDataInfo_.get(index);
                } else {
                    return protoDeviceRealDataInfoBuilder_.getMessageOrBuilder(index);
                }
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public java.util.List<? extends DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder>
            getProtoDeviceRealDataInfoOrBuilderList() {
                if (protoDeviceRealDataInfoBuilder_ != null) {
                    return protoDeviceRealDataInfoBuilder_.getMessageOrBuilderList();
                } else {
                    return java.util.Collections.unmodifiableList(protoDeviceRealDataInfo_);
                }
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder addProtoDeviceRealDataInfoBuilder() {
                return getProtoDeviceRealDataInfoFieldBuilder().addBuilder(
                        DeviceRealDataProto.ProtoDeviceRealDataInfo.getDefaultInstance());
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder addProtoDeviceRealDataInfoBuilder(
                    int index) {
                return getProtoDeviceRealDataInfoFieldBuilder().addBuilder(
                        index, DeviceRealDataProto.ProtoDeviceRealDataInfo.getDefaultInstance());
            }

            /**
             * <code>repeated .ProtoDeviceRealDataInfo protoDeviceRealDataInfo = 1;</code>
             */
            public java.util.List<DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder>
            getProtoDeviceRealDataInfoBuilderList() {
                return getProtoDeviceRealDataInfoFieldBuilder().getBuilderList();
            }

            private com.google.protobuf.RepeatedFieldBuilderV3<
                    DeviceRealDataProto.ProtoDeviceRealDataInfo, DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder, DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder>
            getProtoDeviceRealDataInfoFieldBuilder() {
                if (protoDeviceRealDataInfoBuilder_ == null) {
                    protoDeviceRealDataInfoBuilder_ = new com.google.protobuf.RepeatedFieldBuilderV3<
                            DeviceRealDataProto.ProtoDeviceRealDataInfo, DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder, DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder>(
                            protoDeviceRealDataInfo_,
                            ((bitField0_ & 0x00000001) == 0x00000001),
                            getParentForChildren(),
                            isClean());
                    protoDeviceRealDataInfo_ = null;
                }
                return protoDeviceRealDataInfoBuilder_;
            }

            @java.lang.Override
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.setUnknownFieldsProto3(unknownFields);
            }

            @java.lang.Override
            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.mergeUnknownFields(unknownFields);
            }


            // @@protoc_insertion_point(builder_scope:ProtoDeviceRealDataReceived)
        }

        // @@protoc_insertion_point(class_scope:ProtoDeviceRealDataReceived)
        private static final DeviceRealDataProto.ProtoDeviceRealDataReceived DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new DeviceRealDataProto.ProtoDeviceRealDataReceived();
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataReceived getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ProtoDeviceRealDataReceived>
                PARSER = new com.google.protobuf.AbstractParser<ProtoDeviceRealDataReceived>() {
            @java.lang.Override
            public ProtoDeviceRealDataReceived parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ProtoDeviceRealDataReceived(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ProtoDeviceRealDataReceived> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ProtoDeviceRealDataReceived> getParserForType() {
            return PARSER;
        }

        @java.lang.Override
        public DeviceRealDataProto.ProtoDeviceRealDataReceived getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    public interface ProtoDeviceRealDataInfoOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ProtoDeviceRealDataInfo)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>string deviceId = 1;</code>
         */
        java.lang.String getDeviceId();

        /**
         * <code>string deviceId = 1;</code>
         */
        com.google.protobuf.ByteString
        getDeviceIdBytes();

        /**
         * <code>string timestamp = 2;</code>
         */
        java.lang.String getTimestamp();

        /**
         * <code>string timestamp = 2;</code>
         */
        com.google.protobuf.ByteString
        getTimestampBytes();

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */
        int getTagDataCount();

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */
        boolean containsTagData(
                java.lang.String key);

        /**
         * Use {@link #getTagDataMap()} instead.
         */
        @java.lang.Deprecated
        java.util.Map<java.lang.String, com.google.protobuf.ByteString>
        getTagData();

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */
        java.util.Map<java.lang.String, com.google.protobuf.ByteString>
        getTagDataMap();

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */

        com.google.protobuf.ByteString getTagDataOrDefault(
                java.lang.String key,
                com.google.protobuf.ByteString defaultValue);

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */

        com.google.protobuf.ByteString getTagDataOrThrow(
                java.lang.String key);
    }

    /**
     * Protobuf type {@code ProtoDeviceRealDataInfo}
     */
    public static final class ProtoDeviceRealDataInfo extends
            com.google.protobuf.GeneratedMessageV3 implements
            // @@protoc_insertion_point(message_implements:ProtoDeviceRealDataInfo)
            ProtoDeviceRealDataInfoOrBuilder {
        private static final long serialVersionUID = 0L;

        // Use ProtoDeviceRealDataInfo.newBuilder() to construct.
        private ProtoDeviceRealDataInfo(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
            super(builder);
        }

        private ProtoDeviceRealDataInfo() {
            deviceId_ = "";
            timestamp_ = "";
        }

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet
        getUnknownFields() {
            return this.unknownFields;
        }

        private ProtoDeviceRealDataInfo(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            this();
            if (extensionRegistry == null) {
                throw new java.lang.NullPointerException();
            }
            int mutable_bitField0_ = 0;
            com.google.protobuf.UnknownFieldSet.Builder unknownFields =
                    com.google.protobuf.UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        case 10: {
                            java.lang.String s = input.readStringRequireUtf8();

                            deviceId_ = s;
                            break;
                        }
                        case 18: {
                            java.lang.String s = input.readStringRequireUtf8();

                            timestamp_ = s;
                            break;
                        }
                        case 26: {
                            if (!((mutable_bitField0_ & 0x00000004) == 0x00000004)) {
                                tagData_ = com.google.protobuf.MapField.newMapField(
                                        TagDataDefaultEntryHolder.defaultEntry);
                                mutable_bitField0_ |= 0x00000004;
                            }
                            com.google.protobuf.MapEntry<java.lang.String, com.google.protobuf.ByteString>
                                    tagData__ = input.readMessage(
                                    TagDataDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
                            tagData_.getMutableMap().put(
                                    tagData__.getKey(), tagData__.getValue());
                            break;
                        }
                        default: {
                            if (!parseUnknownFieldProto3(
                                    input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(
                        e).setUnfinishedMessage(this);
            } finally {
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
            return DeviceRealDataProto.internal_static_ProtoDeviceRealDataInfo_descriptor;
        }

        @SuppressWarnings({"rawtypes"})
        @java.lang.Override
        protected com.google.protobuf.MapField internalGetMapField(
                int number) {
            switch (number) {
                case 3:
                    return internalGetTagData();
                default:
                    throw new RuntimeException(
                            "Invalid map field number: " + number);
            }
        }

        @java.lang.Override
        protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
            return DeviceRealDataProto.internal_static_ProtoDeviceRealDataInfo_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(
                            DeviceRealDataProto.ProtoDeviceRealDataInfo.class, DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder.class);
        }

        private int bitField0_;
        public static final int DEVICEID_FIELD_NUMBER = 1;
        private volatile java.lang.Object deviceId_;

        /**
         * <code>string deviceId = 1;</code>
         */
        public java.lang.String getDeviceId() {
            java.lang.Object ref = deviceId_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                deviceId_ = s;
                return s;
            }
        }

        /**
         * <code>string deviceId = 1;</code>
         */
        public com.google.protobuf.ByteString
        getDeviceIdBytes() {
            java.lang.Object ref = deviceId_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                deviceId_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int TIMESTAMP_FIELD_NUMBER = 2;
        private volatile java.lang.Object timestamp_;

        /**
         * <code>string timestamp = 2;</code>
         */
        public java.lang.String getTimestamp() {
            java.lang.Object ref = timestamp_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs =
                        (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                timestamp_ = s;
                return s;
            }
        }

        /**
         * <code>string timestamp = 2;</code>
         */
        public com.google.protobuf.ByteString
        getTimestampBytes() {
            java.lang.Object ref = timestamp_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b =
                        com.google.protobuf.ByteString.copyFromUtf8(
                                (java.lang.String) ref);
                timestamp_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        public static final int TAGDATA_FIELD_NUMBER = 3;

        private static final class TagDataDefaultEntryHolder {
            static final com.google.protobuf.MapEntry<
                    java.lang.String, com.google.protobuf.ByteString> defaultEntry =
                    com.google.protobuf.MapEntry
                            .<java.lang.String, com.google.protobuf.ByteString>newDefaultInstance(
                                    DeviceRealDataProto.internal_static_ProtoDeviceRealDataInfo_TagDataEntry_descriptor,
                                    com.google.protobuf.WireFormat.FieldType.STRING,
                                    "",
                                    com.google.protobuf.WireFormat.FieldType.BYTES,
                                    com.google.protobuf.ByteString.EMPTY);
        }

        private com.google.protobuf.MapField<
                java.lang.String, com.google.protobuf.ByteString> tagData_;

        private com.google.protobuf.MapField<java.lang.String, com.google.protobuf.ByteString>
        internalGetTagData() {
            if (tagData_ == null) {
                return com.google.protobuf.MapField.emptyMapField(
                        TagDataDefaultEntryHolder.defaultEntry);
            }
            return tagData_;
        }

        public int getTagDataCount() {
            return internalGetTagData().getMap().size();
        }

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */

        public boolean containsTagData(
                java.lang.String key) {
            if (key == null) {
                throw new java.lang.NullPointerException();
            }
            return internalGetTagData().getMap().containsKey(key);
        }

        /**
         * Use {@link #getTagDataMap()} instead.
         */
        @java.lang.Deprecated
        public java.util.Map<java.lang.String, com.google.protobuf.ByteString> getTagData() {
            return getTagDataMap();
        }

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */

        public java.util.Map<java.lang.String, com.google.protobuf.ByteString> getTagDataMap() {
            return internalGetTagData().getMap();
        }

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */

        public com.google.protobuf.ByteString getTagDataOrDefault(
                java.lang.String key,
                com.google.protobuf.ByteString defaultValue) {
            if (key == null) {
                throw new java.lang.NullPointerException();
            }
            java.util.Map<java.lang.String, com.google.protobuf.ByteString> map =
                    internalGetTagData().getMap();
            return map.containsKey(key) ? map.get(key) : defaultValue;
        }

        /**
         * <code>map&lt;string, bytes&gt; tagData = 3;</code>
         */

        public com.google.protobuf.ByteString getTagDataOrThrow(
                java.lang.String key) {
            if (key == null) {
                throw new java.lang.NullPointerException();
            }
            java.util.Map<java.lang.String, com.google.protobuf.ByteString> map =
                    internalGetTagData().getMap();
            if (!map.containsKey(key)) {
                throw new java.lang.IllegalArgumentException();
            }
            return map.get(key);
        }

        private byte memoizedIsInitialized = -1;

        @java.lang.Override
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1) return true;
            if (isInitialized == 0) return false;

            memoizedIsInitialized = 1;
            return true;
        }

        @java.lang.Override
        public void writeTo(com.google.protobuf.CodedOutputStream output)
                throws java.io.IOException {
            if (!getDeviceIdBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 1, deviceId_);
            }
            if (!getTimestampBytes().isEmpty()) {
                com.google.protobuf.GeneratedMessageV3.writeString(output, 2, timestamp_);
            }
            com.google.protobuf.GeneratedMessageV3
                    .serializeStringMapTo(
                            output,
                            internalGetTagData(),
                            TagDataDefaultEntryHolder.defaultEntry,
                            3);
            unknownFields.writeTo(output);
        }

        @java.lang.Override
        public int getSerializedSize() {
            int size = memoizedSize;
            if (size != -1) return size;

            size = 0;
            if (!getDeviceIdBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, deviceId_);
            }
            if (!getTimestampBytes().isEmpty()) {
                size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, timestamp_);
            }
            for (java.util.Map.Entry<java.lang.String, com.google.protobuf.ByteString> entry
                    : internalGetTagData().getMap().entrySet()) {
                com.google.protobuf.MapEntry<java.lang.String, com.google.protobuf.ByteString>
                        tagData__ = TagDataDefaultEntryHolder.defaultEntry.newBuilderForType()
                        .setKey(entry.getKey())
                        .setValue(entry.getValue())
                        .build();
                size += com.google.protobuf.CodedOutputStream
                        .computeMessageSize(3, tagData__);
            }
            size += unknownFields.getSerializedSize();
            memoizedSize = size;
            return size;
        }

        @java.lang.Override
        public boolean equals(final java.lang.Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof DeviceRealDataProto.ProtoDeviceRealDataInfo)) {
                return super.equals(obj);
            }
            DeviceRealDataProto.ProtoDeviceRealDataInfo other = (DeviceRealDataProto.ProtoDeviceRealDataInfo) obj;

            boolean result = true;
            result = result && getDeviceId()
                    .equals(other.getDeviceId());
            result = result && getTimestamp()
                    .equals(other.getTimestamp());
            result = result && internalGetTagData().equals(
                    other.internalGetTagData());
            result = result && unknownFields.equals(other.unknownFields);
            return result;
        }

        @java.lang.Override
        public int hashCode() {
            if (memoizedHashCode != 0) {
                return memoizedHashCode;
            }
            int hash = 41;
            hash = (19 * hash) + getDescriptor().hashCode();
            hash = (37 * hash) + DEVICEID_FIELD_NUMBER;
            hash = (53 * hash) + getDeviceId().hashCode();
            hash = (37 * hash) + TIMESTAMP_FIELD_NUMBER;
            hash = (53 * hash) + getTimestamp().hashCode();
            if (!internalGetTagData().getMap().isEmpty()) {
                hash = (37 * hash) + TAGDATA_FIELD_NUMBER;
                hash = (53 * hash) + internalGetTagData().hashCode();
            }
            hash = (29 * hash) + unknownFields.hashCode();
            memoizedHashCode = hash;
            return hash;
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(
                java.nio.ByteBuffer data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(
                java.nio.ByteBuffer data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(
                com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(
                com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(
                byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseDelimitedFrom(
                java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(
                com.google.protobuf.CodedInputStream input)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input);
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo parseFrom(
                com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws java.io.IOException {
            return com.google.protobuf.GeneratedMessageV3
                    .parseWithIOException(PARSER, input, extensionRegistry);
        }

        @java.lang.Override
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(DeviceRealDataProto.ProtoDeviceRealDataInfo prototype) {
            return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
        }

        @java.lang.Override
        public Builder toBuilder() {
            return this == DEFAULT_INSTANCE
                    ? new Builder() : new Builder().mergeFrom(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(
                com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code ProtoDeviceRealDataInfo}
         */
        public static final class Builder extends
                com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ProtoDeviceRealDataInfo)
                DeviceRealDataProto.ProtoDeviceRealDataInfoOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor
            getDescriptor() {
                return DeviceRealDataProto.internal_static_ProtoDeviceRealDataInfo_descriptor;
            }

            @SuppressWarnings({"rawtypes"})
            protected com.google.protobuf.MapField internalGetMapField(
                    int number) {
                switch (number) {
                    case 3:
                        return internalGetTagData();
                    default:
                        throw new RuntimeException(
                                "Invalid map field number: " + number);
                }
            }

            @SuppressWarnings({"rawtypes"})
            protected com.google.protobuf.MapField internalGetMutableMapField(
                    int number) {
                switch (number) {
                    case 3:
                        return internalGetMutableTagData();
                    default:
                        throw new RuntimeException(
                                "Invalid map field number: " + number);
                }
            }

            @java.lang.Override
            protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internalGetFieldAccessorTable() {
                return DeviceRealDataProto.internal_static_ProtoDeviceRealDataInfo_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(
                                DeviceRealDataProto.ProtoDeviceRealDataInfo.class, DeviceRealDataProto.ProtoDeviceRealDataInfo.Builder.class);
            }

            // Construct using DeviceRealDataProto.ProtoDeviceRealDataInfo.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(
                    com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessageV3
                        .alwaysUseFieldBuilders) {
                }
            }

            @java.lang.Override
            public Builder clear() {
                super.clear();
                deviceId_ = "";

                timestamp_ = "";

                internalGetMutableTagData().clear();
                return this;
            }

            @java.lang.Override
            public com.google.protobuf.Descriptors.Descriptor
            getDescriptorForType() {
                return DeviceRealDataProto.internal_static_ProtoDeviceRealDataInfo_descriptor;
            }

            @java.lang.Override
            public DeviceRealDataProto.ProtoDeviceRealDataInfo getDefaultInstanceForType() {
                return DeviceRealDataProto.ProtoDeviceRealDataInfo.getDefaultInstance();
            }

            @java.lang.Override
            public DeviceRealDataProto.ProtoDeviceRealDataInfo build() {
                DeviceRealDataProto.ProtoDeviceRealDataInfo result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            @java.lang.Override
            public DeviceRealDataProto.ProtoDeviceRealDataInfo buildPartial() {
                DeviceRealDataProto.ProtoDeviceRealDataInfo result = new DeviceRealDataProto.ProtoDeviceRealDataInfo(this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                result.deviceId_ = deviceId_;
                result.timestamp_ = timestamp_;
                result.tagData_ = internalGetTagData();
                result.tagData_.makeImmutable();
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @java.lang.Override
            public Builder clone() {
                return (Builder) super.clone();
            }

            @java.lang.Override
            public Builder setField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return (Builder) super.setField(field, value);
            }

            @java.lang.Override
            public Builder clearField(
                    com.google.protobuf.Descriptors.FieldDescriptor field) {
                return (Builder) super.clearField(field);
            }

            @java.lang.Override
            public Builder clearOneof(
                    com.google.protobuf.Descriptors.OneofDescriptor oneof) {
                return (Builder) super.clearOneof(oneof);
            }

            @java.lang.Override
            public Builder setRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    int index, java.lang.Object value) {
                return (Builder) super.setRepeatedField(field, index, value);
            }

            @java.lang.Override
            public Builder addRepeatedField(
                    com.google.protobuf.Descriptors.FieldDescriptor field,
                    java.lang.Object value) {
                return (Builder) super.addRepeatedField(field, value);
            }

            @java.lang.Override
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof DeviceRealDataProto.ProtoDeviceRealDataInfo) {
                    return mergeFrom((DeviceRealDataProto.ProtoDeviceRealDataInfo) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(DeviceRealDataProto.ProtoDeviceRealDataInfo other) {
                if (other == DeviceRealDataProto.ProtoDeviceRealDataInfo.getDefaultInstance()) return this;
                if (!other.getDeviceId().isEmpty()) {
                    deviceId_ = other.deviceId_;
                    onChanged();
                }
                if (!other.getTimestamp().isEmpty()) {
                    timestamp_ = other.timestamp_;
                    onChanged();
                }
                internalGetMutableTagData().mergeFrom(
                        other.internalGetTagData());
                this.mergeUnknownFields(other.unknownFields);
                onChanged();
                return this;
            }

            @java.lang.Override
            public final boolean isInitialized() {
                return true;
            }

            @java.lang.Override
            public Builder mergeFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                DeviceRealDataProto.ProtoDeviceRealDataInfo parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (DeviceRealDataProto.ProtoDeviceRealDataInfo) e.getUnfinishedMessage();
                    throw e.unwrapIOException();
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            private java.lang.Object deviceId_ = "";

            /**
             * <code>string deviceId = 1;</code>
             */
            public java.lang.String getDeviceId() {
                java.lang.Object ref = deviceId_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    deviceId_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string deviceId = 1;</code>
             */
            public com.google.protobuf.ByteString
            getDeviceIdBytes() {
                java.lang.Object ref = deviceId_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    deviceId_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string deviceId = 1;</code>
             */
            public Builder setDeviceId(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                deviceId_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string deviceId = 1;</code>
             */
            public Builder clearDeviceId() {

                deviceId_ = getDefaultInstance().getDeviceId();
                onChanged();
                return this;
            }

            /**
             * <code>string deviceId = 1;</code>
             */
            public Builder setDeviceIdBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                deviceId_ = value;
                onChanged();
                return this;
            }

            private java.lang.Object timestamp_ = "";

            /**
             * <code>string timestamp = 2;</code>
             */
            public java.lang.String getTimestamp() {
                java.lang.Object ref = timestamp_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs =
                            (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    timestamp_ = s;
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>string timestamp = 2;</code>
             */
            public com.google.protobuf.ByteString
            getTimestampBytes() {
                java.lang.Object ref = timestamp_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b =
                            com.google.protobuf.ByteString.copyFromUtf8(
                                    (java.lang.String) ref);
                    timestamp_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>string timestamp = 2;</code>
             */
            public Builder setTimestamp(
                    java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }

                timestamp_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>string timestamp = 2;</code>
             */
            public Builder clearTimestamp() {

                timestamp_ = getDefaultInstance().getTimestamp();
                onChanged();
                return this;
            }

            /**
             * <code>string timestamp = 2;</code>
             */
            public Builder setTimestampBytes(
                    com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                checkByteStringIsUtf8(value);

                timestamp_ = value;
                onChanged();
                return this;
            }

            private com.google.protobuf.MapField<
                    java.lang.String, com.google.protobuf.ByteString> tagData_;

            private com.google.protobuf.MapField<java.lang.String, com.google.protobuf.ByteString>
            internalGetTagData() {
                if (tagData_ == null) {
                    return com.google.protobuf.MapField.emptyMapField(
                            TagDataDefaultEntryHolder.defaultEntry);
                }
                return tagData_;
            }

            private com.google.protobuf.MapField<java.lang.String, com.google.protobuf.ByteString>
            internalGetMutableTagData() {
                onChanged();
                ;
                if (tagData_ == null) {
                    tagData_ = com.google.protobuf.MapField.newMapField(
                            TagDataDefaultEntryHolder.defaultEntry);
                }
                if (!tagData_.isMutable()) {
                    tagData_ = tagData_.copy();
                }
                return tagData_;
            }

            public int getTagDataCount() {
                return internalGetTagData().getMap().size();
            }

            /**
             * <code>map&lt;string, bytes&gt; tagData = 3;</code>
             */

            public boolean containsTagData(
                    java.lang.String key) {
                if (key == null) {
                    throw new java.lang.NullPointerException();
                }
                return internalGetTagData().getMap().containsKey(key);
            }

            /**
             * Use {@link #getTagDataMap()} instead.
             */
            @java.lang.Deprecated
            public java.util.Map<java.lang.String, com.google.protobuf.ByteString> getTagData() {
                return getTagDataMap();
            }

            /**
             * <code>map&lt;string, bytes&gt; tagData = 3;</code>
             */

            public java.util.Map<java.lang.String, com.google.protobuf.ByteString> getTagDataMap() {
                return internalGetTagData().getMap();
            }

            /**
             * <code>map&lt;string, bytes&gt; tagData = 3;</code>
             */

            public com.google.protobuf.ByteString getTagDataOrDefault(
                    java.lang.String key,
                    com.google.protobuf.ByteString defaultValue) {
                if (key == null) {
                    throw new java.lang.NullPointerException();
                }
                java.util.Map<java.lang.String, com.google.protobuf.ByteString> map =
                        internalGetTagData().getMap();
                return map.containsKey(key) ? map.get(key) : defaultValue;
            }

            /**
             * <code>map&lt;string, bytes&gt; tagData = 3;</code>
             */

            public com.google.protobuf.ByteString getTagDataOrThrow(
                    java.lang.String key) {
                if (key == null) {
                    throw new java.lang.NullPointerException();
                }
                java.util.Map<java.lang.String, com.google.protobuf.ByteString> map =
                        internalGetTagData().getMap();
                if (!map.containsKey(key)) {
                    throw new java.lang.IllegalArgumentException();
                }
                return map.get(key);
            }

            public Builder clearTagData() {
                internalGetMutableTagData().getMutableMap()
                        .clear();
                return this;
            }

            /**
             * <code>map&lt;string, bytes&gt; tagData = 3;</code>
             */

            public Builder removeTagData(
                    java.lang.String key) {
                if (key == null) {
                    throw new java.lang.NullPointerException();
                }
                internalGetMutableTagData().getMutableMap()
                        .remove(key);
                return this;
            }

            /**
             * Use alternate mutation accessors instead.
             */
            @java.lang.Deprecated
            public java.util.Map<java.lang.String, com.google.protobuf.ByteString>
            getMutableTagData() {
                return internalGetMutableTagData().getMutableMap();
            }

            /**
             * <code>map&lt;string, bytes&gt; tagData = 3;</code>
             */
            public Builder putTagData(
                    java.lang.String key,
                    com.google.protobuf.ByteString value) {
                if (key == null) {
                    throw new java.lang.NullPointerException();
                }
                if (value == null) {
                    throw new java.lang.NullPointerException();
                }
                internalGetMutableTagData().getMutableMap()
                        .put(key, value);
                return this;
            }

            /**
             * <code>map&lt;string, bytes&gt; tagData = 3;</code>
             */

            public Builder putAllTagData(
                    java.util.Map<java.lang.String, com.google.protobuf.ByteString> values) {
                internalGetMutableTagData().getMutableMap()
                        .putAll(values);
                return this;
            }

            @java.lang.Override
            public final Builder setUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.setUnknownFieldsProto3(unknownFields);
            }

            @java.lang.Override
            public final Builder mergeUnknownFields(
                    final com.google.protobuf.UnknownFieldSet unknownFields) {
                return super.mergeUnknownFields(unknownFields);
            }


            // @@protoc_insertion_point(builder_scope:ProtoDeviceRealDataInfo)
        }

        // @@protoc_insertion_point(class_scope:ProtoDeviceRealDataInfo)
        private static final DeviceRealDataProto.ProtoDeviceRealDataInfo DEFAULT_INSTANCE;

        static {
            DEFAULT_INSTANCE = new DeviceRealDataProto.ProtoDeviceRealDataInfo();
        }

        public static DeviceRealDataProto.ProtoDeviceRealDataInfo getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        private static final com.google.protobuf.Parser<ProtoDeviceRealDataInfo>
                PARSER = new com.google.protobuf.AbstractParser<ProtoDeviceRealDataInfo>() {
            @java.lang.Override
            public ProtoDeviceRealDataInfo parsePartialFrom(
                    com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws com.google.protobuf.InvalidProtocolBufferException {
                return new ProtoDeviceRealDataInfo(input, extensionRegistry);
            }
        };

        public static com.google.protobuf.Parser<ProtoDeviceRealDataInfo> parser() {
            return PARSER;
        }

        @java.lang.Override
        public com.google.protobuf.Parser<ProtoDeviceRealDataInfo> getParserForType() {
            return PARSER;
        }

        @java.lang.Override
        public DeviceRealDataProto.ProtoDeviceRealDataInfo getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

    }

    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ProtoDeviceRealDataReceived_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ProtoDeviceRealDataReceived_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ProtoDeviceRealDataInfo_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ProtoDeviceRealDataInfo_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor
            internal_static_ProtoDeviceRealDataInfo_TagDataEntry_descriptor;
    private static final
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
            internal_static_ProtoDeviceRealDataInfo_TagDataEntry_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor
    getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor
            descriptor;

    static {
        java.lang.String[] descriptorData = {
                "\n\037protofiles/DeviceRealData.proto\"X\n\033Pro" +
                        "toDeviceRealDataReceived\0229\n\027protoDeviceR" +
                        "ealDataInfo\030\001 \003(\0132\030.ProtoDeviceRealDataI" +
                        "nfo\"\246\001\n\027ProtoDeviceRealDataInfo\022\020\n\010devic" +
                        "eId\030\001 \001(\t\022\021\n\ttimestamp\030\002 \001(\t\0226\n\007tagData\030" +
                        "\003 \003(\0132%.ProtoDeviceRealDataInfo.TagDataE" +
                        "ntry\032.\n\014TagDataEntry\022\013\n\003key\030\001 \001(\t\022\r\n\005val" +
                        "ue\030\002 \001(\014:\0028\001B\025B\023DeviceRealDataProtob\006pro" +
                        "to3"
        };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
                new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
                    public com.google.protobuf.ExtensionRegistry assignDescriptors(
                            com.google.protobuf.Descriptors.FileDescriptor root) {
                        descriptor = root;
                        return null;
                    }
                };
        com.google.protobuf.Descriptors.FileDescriptor
                .internalBuildGeneratedFileFrom(descriptorData,
                        new com.google.protobuf.Descriptors.FileDescriptor[]{
                        }, assigner);
        internal_static_ProtoDeviceRealDataReceived_descriptor =
                getDescriptor().getMessageTypes().get(0);
        internal_static_ProtoDeviceRealDataReceived_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ProtoDeviceRealDataReceived_descriptor,
                new java.lang.String[]{"ProtoDeviceRealDataInfo",});
        internal_static_ProtoDeviceRealDataInfo_descriptor =
                getDescriptor().getMessageTypes().get(1);
        internal_static_ProtoDeviceRealDataInfo_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ProtoDeviceRealDataInfo_descriptor,
                new java.lang.String[]{"DeviceId", "Timestamp", "TagData",});
        internal_static_ProtoDeviceRealDataInfo_TagDataEntry_descriptor =
                internal_static_ProtoDeviceRealDataInfo_descriptor.getNestedTypes().get(0);
        internal_static_ProtoDeviceRealDataInfo_TagDataEntry_fieldAccessorTable = new
                com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
                internal_static_ProtoDeviceRealDataInfo_TagDataEntry_descriptor,
                new java.lang.String[]{"Key", "Value",});
    }

    // @@protoc_insertion_point(outer_class_scope)
}
